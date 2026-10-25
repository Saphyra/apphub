package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.trading.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemIdAssembler;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.item.ItemLocationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
class CommodityConverter extends ConverterBase<CommodityEntity, Commodity> {
    private final UuidConverter uuidConverter;
    private final ItemIdAssembler itemIdAssembler;

    @Override
    protected CommodityEntity processDomainConversion(Commodity domain) {
        String itemName = domain.getItemName().toLowerCase();

        return CommodityEntity.builder()
            .id(itemIdAssembler.assembleId(domain))
            .itemName(itemName)
            .stationId(uuidConverter.convertDomain(domain.getStationId()))
            .fleetCarrierId(uuidConverter.convertDomain(domain.getFleetCarrierId()))
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
        UUID externalReference = null;
        ItemLocationType locationType = ItemLocationType.UNKNOWN;
        if (nonNull(entity.getStationId())) {
            externalReference = uuidConverter.convertEntity(entity.getStationId());
            locationType = ItemLocationType.STATION;
        } else if (nonNull(entity.getFleetCarrierId())) {
            externalReference = uuidConverter.convertEntity(entity.getFleetCarrierId());
            locationType = ItemLocationType.FLEET_CARRIER;
        }

        return Commodity.builder()
            .externalReference(externalReference)
            .itemName(entity.getItemName())
            .locationType(locationType)
            .marketId(entity.getMarketId())
            .buyPrice(entity.getBuyPrice())
            .sellPrice(entity.getSellPrice())
            .demand(entity.getDemand())
            .stock(entity.getStock())
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .build();
    }
}
