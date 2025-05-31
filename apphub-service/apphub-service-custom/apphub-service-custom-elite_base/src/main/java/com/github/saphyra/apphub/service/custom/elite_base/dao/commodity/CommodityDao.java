package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.DefaultColumn;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.QualifiedTable;
import com.github.saphyra.apphub.service.custom.elite_base.util.sql.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_COMMODITY_NAME;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_COMMODITY;

@Component
@Slf4j
public class CommodityDao extends AbstractDao<CommodityEntity, Commodity, CommodityEntityId, CommodityRepository> {
    private final Set<String> commodityCache = ConcurrentHashMap.newKeySet();
    private volatile boolean loaded = false;

    private final JdbcTemplate jdbcTemplate;

    CommodityDao(CommodityConverter converter, CommodityRepository repository, JdbcTemplate jdbcTemplate) {
        super(converter, repository);
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Commodity> getByMarketIdAndType(Long marketId, CommodityType type) {
        return converter.convertEntity(repository.getByMarketIdAndType(marketId, type));
    }

    public List<String> getCommodities() {
        if (loaded) {
            return new ArrayList<>(commodityCache);
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

        commodityCache.addAll(result);
        loaded = true;

        return result;
    }

    public List<Commodity> findSuppliers(String commodityName, Integer minStock, Integer minPrice, Integer maxPrice) {
        return converter.convertEntity(repository.getSellOffers(commodityName, minStock, minPrice, maxPrice));
    }

    public List<Commodity> findConsumers(String commodityName, Integer minDemand, Integer minPrice, Integer maxPrice) {
        return converter.convertEntity(repository.getBuyOffers(commodityName, minDemand, minPrice, maxPrice));
    }

    @Override
    public void save(Commodity domain) {
        commodityCache.add(domain.getCommodityName());
        super.save(domain);
    }

    @Override
    public void saveAll(List<Commodity> domains) {
        commodityCache.addAll(domains.stream().map(Commodity::getCommodityName).toList());

        super.saveAll(domains);
    }
}
