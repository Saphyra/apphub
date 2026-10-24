package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.dao.ListCachedBufferedDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemDomainId;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.Tradeable;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.TradeableDao;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class FcMaterialDao extends ListCachedBufferedDao<FcMaterialEntity, FcMaterial, String, Long, ItemDomainId, FcMaterialRepository> implements TradeableDao {
    FcMaterialDao(
        FcMaterialConverter converter,
        FcMaterialRepository repository,
        Cache<Long, List<FcMaterial>> readCache,
        FcMaterialWriteBuffer writeBuffer,
        FcMaterialDeleteBuffer deleteBuffer
    ) {
        super(converter, repository, readCache, writeBuffer, deleteBuffer);
    }

    @Override
    protected Long getCacheKey(FcMaterial fcMaterial) {
        return fcMaterial.getMarketId();
    }

    @Override
    protected ItemDomainId toDomainId(String id) {
        return repository.findById(id)
            .map(converter::convertEntity)
            .map(this::getDomainId)
            .orElseGet(() -> ItemDomainId.builder().entityId(id).build());
    }

    @Override
    protected ItemDomainId getDomainId(FcMaterial fcMaterial) {
        return ItemDomainId.builder()
            .externalReference(fcMaterial.getExternalReference())
            .itemName(fcMaterial.getItemName())
            .locationType(fcMaterial.getLocationType())
            .build();
    }

    @Override
    protected boolean matchesWithId(ItemDomainId domainId, FcMaterial fcMaterial) {
        return Objects.equals(fcMaterial.getExternalReference(), domainId.getExternalReference())
            && Objects.equals(fcMaterial.getItemName(), domainId.getItemName())
            && Objects.equals(fcMaterial.getLocationType(), domainId.getLocationType());
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
}
