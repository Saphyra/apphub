package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemEntityId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CommodityConverter extends ConverterBase<CommodityEntity, Commodity> {
    private final UuidConverter uuidConverter;

    @Override
    protected CommodityEntity processDomainConversion(Commodity domain) {
        return CommodityEntity.builder()
            .id(ItemEntityId.builder()
                .itemName(domain.getItemName().toLowerCase())
                .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
                .build())
            .locationType(domain.getLocationType())
            .marketId(domain.getMarketId())
            .buyPrice(domain.getBuyPrice())
            .sellPrice(domain.getSellPrice())
            .demand(domain.getDemand())
            .stock(domain.getStock())
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .build();
    }

    @Override
    protected Commodity processEntityConversion(CommodityEntity entity) {
        return Commodity.builder()
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
