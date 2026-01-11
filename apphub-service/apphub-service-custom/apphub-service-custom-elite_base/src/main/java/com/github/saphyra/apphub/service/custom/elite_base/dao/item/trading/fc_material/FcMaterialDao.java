package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

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
public class FcMaterialDao extends ListCachedBufferedDao<FcMaterialEntity, FcMaterial, ItemEntityId, Long, ItemDomainId, FcMaterialRepository> implements TradeableDao {
    private final UuidConverter uuidConverter;

    FcMaterialDao(
        FcMaterialConverter converter,
        FcMaterialRepository repository,
        Cache<Long, List<FcMaterial>> readCache,
        FcMaterialWriteBuffer writeBuffer,
        FcMaterialDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected Long getCacheKey(FcMaterial fcMaterial) {
        return fcMaterial.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(ItemEntityId itemEntityId) {
        return ItemDomainId.builder()
            .externalReference(uuidConverter.convertEntity(itemEntityId.getExternalReference()))
            .itemName(itemEntityId.getItemName())
            .build();
    }

    @Override
    protected ItemDomainId getDomainId(FcMaterial fcMaterial) {
        return ItemDomainId.builder()
            .externalReference(fcMaterial.getExternalReference())
            .itemName(fcMaterial.getItemName())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, FcMaterial fcMaterial) {
        return fcMaterial.getExternalReference().equals(domainId.getExternalReference())
            && fcMaterial.getItemName().equals(domainId.getItemName());
    }

    @Override
    public List<FcMaterial> getByMarketId(Long marketId) {
        return searchList(marketId, () -> repository.getByMarketId(marketId));
    }

    @Override
    public void deleteAllTradeables(List<Tradeable> tradeables) {
        List<FcMaterial> materials = tradeables.stream()
            .map(tradeable -> (FcMaterial) tradeable)
            .toList();
        deleteAll(materials);
    }

    @Override
    public void saveAll(List<Tradeable> tradeables) {
        List<FcMaterial> materials = tradeables.stream()
            .map(tradeable -> (FcMaterial) tradeable)
            .toList();
        saveAll(materials);
    }

    @Override
    public List<FcMaterial> getSuppliers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice) {
        List<FcMaterialEntity> entities = repository.getSellOffers(itemName, minTradeAmount, minPrice, maxPrice);
        log.info("Found {} FcMaterial SellOffers", entities.size());
        List<FcMaterial> domains = converter.convertEntity(entities);
        log.info("FcMaterials converted");
        return syncWithCaches(domains);
    }

    @Override
    public List<FcMaterial> getConsumers(String itemName, Integer minTradeAmount, Integer minPrice, Integer maxPrice) {
        List<FcMaterialEntity> entities = repository.getBuyOffers(itemName, minTradeAmount, minPrice, maxPrice);
        log.info("Found {} FcMaterial SellOffers", entities.size());
        List<FcMaterial> domains = converter.convertEntity(entities);
        log.info("FcMaterials converted");
        return syncWithCaches(domains);
    }
}
