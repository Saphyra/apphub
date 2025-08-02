package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_COMMODITY_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_COMMODITY;

@Component
@Slf4j
public class CommodityDao extends ListCachedBufferedDao<CommodityEntity, Commodity, CommodityEntityId, CommodityCacheKey, CommodityDomainId, CommodityRepository> {
    private final Set<String> commodityNameCache = ConcurrentHashMap.newKeySet();
    private volatile boolean commodityNamesLoaded = false;

    private final UuidConverter uuidConverter;
    private final JdbcTemplate jdbcTemplate;

    CommodityDao(
        CommodityConverter converter,
        CommodityRepository repository,
        Cache<CommodityCacheKey, List<Commodity>> commodityReadCache,
        CommodityWriteBuffer writeBuffer,
        CommodityDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter,
        JdbcTemplate jdbcTemplate
    ) {
        super(
            converter,
            repository,
            commodityReadCache,
            writeBuffer,
            deleteBuffer
        );
        this.uuidConverter = uuidConverter;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Commodity> getByMarketIdAndType(Long marketId, CommodityType type) {
        CommodityCacheKey cacheKey = CommodityCacheKey.builder()
            .marketId(marketId)
            .commodityType(type)
            .build();

        return searchList(cacheKey, () -> repository.getByMarketIdAndType(marketId, type));
    }

    public List<String> getCommodityNames() {
        if (commodityNamesLoaded) {
            return new ArrayList<>(commodityNameCache);
        }

        String sql = SqlBuilder.select()
            .column(new DefaultColumn(COLUMN_COMMODITY_NAME))
            .from(new QualifiedTable(SCHEMA, TABLE_COMMODITY))
            .groupBy(new DefaultColumn(COLUMN_COMMODITY_NAME))
            .build();
        log.debug(sql);

        List<String> result = jdbcTemplate.query(sql, rs -> {
            List<String> r = new ArrayList<>();

            while (rs.next()) {
                r.add(rs.getString(COLUMN_COMMODITY_NAME));
            }

            return r;
        });

        commodityNameCache.addAll(result);
        commodityNamesLoaded = true;

        return result;
    }

    public List<Commodity> findSuppliers(String commodityName, Integer minStock, Integer minPrice, Integer maxPrice) {
        return syncWithCaches(converter.convertEntity(repository.getSellOffers(commodityName, minStock, minPrice, maxPrice)));
    }

    public List<Commodity> findConsumers(String commodityName, Integer minDemand, Integer minPrice, Integer maxPrice) {
        return syncWithCaches(converter.convertEntity(repository.getBuyOffers(commodityName, minDemand, minPrice, maxPrice)));
    }

    @Override
    public void save(Commodity domain) {
        commodityNameCache.add(domain.getCommodityName());
        super.save(domain);
    }

    @Override
    protected CommodityCacheKey getCacheKey(Commodity commodity) {
        return CommodityCacheKey.builder()
            .marketId(commodity.getMarketId())
            .commodityType(commodity.getType())
            .build();
    }

    @Override
    protected CommodityDomainId toDomainId(CommodityEntityId commodityEntityId) {
        return CommodityDomainId.builder()
            .externalReference(uuidConverter.convertEntity(commodityEntityId.getExternalReference()))
            .commodityName(commodityEntityId.getCommodityName())
            .build();
    }

    @Override
    protected CommodityDomainId getDomainId(Commodity commodity) {
        return CommodityDomainId.builder()
            .externalReference(commodity.getExternalReference())
            .commodityName(commodity.getCommodityName())
            .build();
    }

    @Override
    protected boolean matchesWithId(CommodityDomainId domainId, Commodity commodity) {
        return commodity.getExternalReference().equals(domainId.getExternalReference())
            && commodity.getCommodityName().equals(domainId.getCommodityName());
    }

    @Override
    public void saveAll(Collection<Commodity> domains) {
        commodityNameCache.addAll(domains.stream().map(Commodity::getCommodityName).toList());

        super.saveAll(domains);
    }
}
