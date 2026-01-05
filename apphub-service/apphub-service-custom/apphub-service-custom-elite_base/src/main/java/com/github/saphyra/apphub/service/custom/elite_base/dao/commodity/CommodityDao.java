package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class CommodityDao extends ListCachedBufferedDao<CommodityEntity, Commodity, CommodityEntityId, CommodityCacheKey, CommodityDomainId, CommodityRepository> {
    private final UuidConverter uuidConverter;
    private final CommodityNameCache commodityNameCache;
    private final ExecutorServiceBean executorServiceBean;

    CommodityDao(
        CommodityConverter converter,
        CommodityRepository repository,
        Cache<CommodityCacheKey, List<Commodity>> commodityReadCache,
        CommodityWriteBuffer writeBuffer,
        CommodityDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter,
        CommodityNameCache commodityNameCache,
        ExecutorServiceBean executorServiceBean
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
        this.executorServiceBean = executorServiceBean;
    }

    public List<Commodity> getByMarketIdAndType(Long marketId, CommodityType type) {
        CommodityCacheKey cacheKey = CommodityCacheKey.builder()
            .marketId(marketId)
            .commodityType(type)
            .build();

        return searchList(cacheKey, () -> repository.getByMarketIdAndType(marketId, type));
    }

    public List<Commodity> findSuppliers(String commodityName, Integer minStock, Integer minPrice, Integer maxPrice) {
        List<CommodityEntity> sellOffers = repository.getSellOffers(commodityName, minStock, minPrice, maxPrice);
        log.info("Found {} sellOffers", sellOffers.size());
        List<Commodity> original = sellOffers.stream()
            .map(entity -> CompletableFuture.supplyAsync(() -> converter.convertEntity(entity), executorServiceBean.getExecutor()))
            .map(CompletableFuture::join)
            .toList();
        log.info("SellOffers converted");
        return syncWithCaches(original);
    }

    public List<Commodity> findConsumers(String commodityName, Integer minDemand, Integer minPrice, Integer maxPrice) {
        List<CommodityEntity> buyOffers = repository.getBuyOffers(commodityName, minDemand, minPrice, maxPrice);
        log.info("Found {} buyOffers", buyOffers.size());
        List<Commodity> original = buyOffers.stream()
            .map(entity -> CompletableFuture.supplyAsync(() -> converter.convertEntity(entity), executorServiceBean.getExecutor()))
            .map(CompletableFuture::join)
            .toList();
        log.info("BuyOffers converted");
        return syncWithCaches(original);
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
