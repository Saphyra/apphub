package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.fc_material;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class FcMaterialConverter extends ConverterBase<FcMaterialEntity, FcMaterial> {
    private final UuidConverter uuidConverter;

    @Override
    protected FcMaterialEntity processDomainConversion(FcMaterial domain) {
        return FcMaterialEntity.builder()
            .id(ItemEntityId.builder()
                .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
                .itemName(domain.getItemName())
                .build())
            .marketId(domain.getMarketId())
            .locationType(domain.getLocationType())
            .buyPrice(domain.getBuyPrice())
            .sellPrice(domain.getSellPrice())
            .demand(domain.getDemand())
            .stock(domain.getStock())
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .build();
    }

    @Override
    protected FcMaterial processEntityConversion(FcMaterialEntity entity) {
        return FcMaterial.builder()
            .externalReference(uuidConverter.convertEntity(entity.getId().getExternalReference()))
            .itemName(entity.getId().getItemName())
            .locationType(entity.getLocationType())
            .marketId(entity.getMarketId())
            .buyPrice(entity.getBuyPrice())
            .sellPrice(entity.getSellPrice())
            .demand(entity.getDemand())
            .stock(entity.getStock())
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .build();
    }
}
