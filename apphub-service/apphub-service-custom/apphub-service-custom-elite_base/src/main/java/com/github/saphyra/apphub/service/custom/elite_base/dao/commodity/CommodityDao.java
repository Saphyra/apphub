package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class CommodityDao extends ListCachedBufferedDao<CommodityEntity, Commodity, CommodityEntityId, CommodityCacheKey, CommodityDomainId, CommodityRepository> {
    private final UuidConverter uuidConverter;
    private final CommodityNameCache commodityNameCache;

    CommodityDao(
        CommodityConverter converter,
        CommodityRepository repository,
        Cache<CommodityCacheKey, List<Commodity>> commodityReadCache,
        CommodityWriteBuffer writeBuffer,
        CommodityDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter,
        CommodityNameCache commodityNameCache
    ) {
        super(
            converter,
            repository,
            commodityReadCache,
            writeBuffer,
            deleteBuffer
        );
        this.uuidConverter = uuidConverter;
        this.commodityNameCache = commodityNameCache;
    }

    public List<Commodity> getByMarketIdAndType(Long marketId, CommodityType type) {
        CommodityCacheKey cacheKey = CommodityCacheKey.builder()
            .marketId(marketId)
            .commodityType(type)
            .build();

        return searchList(cacheKey, () -> repository.getByMarketIdAndType(marketId, type));
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
