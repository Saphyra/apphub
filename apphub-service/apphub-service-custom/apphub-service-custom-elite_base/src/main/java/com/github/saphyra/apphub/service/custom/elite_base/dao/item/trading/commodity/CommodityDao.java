package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.TradeableDao;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
//TODO unit test
public class CommodityDao extends ListCachedBufferedDao<CommodityEntity, Commodity, ItemEntityId, Long, ItemDomainId, CommodityRepository> implements TradeableDao {
    private final UuidConverter uuidConverter;

    CommodityDao(
        CommodityConverter converter,
        CommodityRepository repository,
        Cache<Long, List<Commodity>> readCache,
        CommodityWriteBuffer writeBuffer,
        CommodityDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected Long getCacheKey(Commodity commodity) {
        return commodity.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(ItemEntityId itemEntityId) {
        return ItemDomainId.builder()
            .externalReference(uuidConverter.convertEntity(itemEntityId.getExternalReference()))
            .itemName(itemEntityId.getItemName())
            .build();
    }

    @Override
    protected ItemDomainId getDomainId(Commodity commodity) {
        return ItemDomainId.builder()
            .externalReference(commodity.getExternalReference())
            .itemName(commodity.getItemName())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, Commodity commodity) {
        return commodity.getExternalReference().equals(domainId.getExternalReference())
            && commodity.getItemName().equals(domainId.getItemName());
    }

    @Override
    public List<Commodity> getByMarketId(Long marketId) {
        return searchList(marketId, () -> repository.getByMarketId(marketId));
    }

    @Override
    public void deleteAllTradeables(List<Tradeable> tradeables) {
        List<Commodity> commodities = tradeables.stream()
            .map(tradeable -> (Commodity) tradeable)
            .toList();
        deleteAll(commodities);
    }

    @Override
    public void saveAll(List<Tradeable> tradeables) {
        List<Commodity> commodities = tradeables.stream()
            .map(tradeable -> (Commodity) tradeable)
            .toList();
        saveAll(commodities);
    }

    @Override
    public List<Commodity> getSuppliers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice) {
        List<CommodityEntity> entities = repository.getSellOffers(itemName, minTradeAmount, minPrice, maxPrice);
        log.info("Found {} Commodity SellOffers", entities.size());
        List<Commodity> domains = converter.convertEntity(entities);
        log.info("Commodities converted");
        return syncWithCaches(domains);
    }

    @Override
    public List<Commodity> getConsumers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice) {
        List<CommodityEntity> entities = repository.getBuyOffers(itemName, minTradeAmount, minPrice, maxPrice);
        log.info("Found {} Commodity SellOffers", entities.size());
        List<Commodity> domains = converter.convertEntity(entities);
        log.info("Commodities converted");
        return syncWithCaches(domains);
    }
}
