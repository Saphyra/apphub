package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price.CommodityAveragePriceFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdate;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.LastUpdateId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CommodityConverter extends ConverterBase<CommodityEntity, Commodity> {
    private final UuidConverter uuidConverter;
    private final CommodityAveragePriceDao commodityAveragePriceDao;
    private final CommodityAveragePriceFactory commodityAveragePriceFactory;
    private final LastUpdateDao lastUpdateDao;

    @Override
    protected CommodityEntity processDomainConversion(Commodity domain) {
        commodityAveragePriceDao.save(commodityAveragePriceFactory.create(domain.getLastUpdate(), domain.getCommodityName(), domain.getAveragePrice()));

        return CommodityEntity.builder()
            .id(CommodityEntityId.builder()
                .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
                .commodityName(domain.getCommodityName())
                .build())
            .type(domain.getType())
            .commodityLocation(domain.getCommodityLocation())
            .marketId(domain.getMarketId())
            .buyPrice(domain.getBuyPrice())
            .sellPrice(domain.getSellPrice())
            .demand(domain.getDemand())
            .stock(domain.getStock())
            .build();
    }

    @Override
    protected Commodity processEntityConversion(CommodityEntity entity) {
        String externalReference = entity.getId().getExternalReference();
        String commodityName = entity.getId().getCommodityName();
        return Commodity.builder()
            .lastUpdate(lastUpdateDao.findById(LastUpdateId.builder()
                    .externalReference(externalReference)
                    .type(entity.getType().get())
                    .build())
                .map(LastUpdate::getLastUpdate)
                .orElse(null))
            .type(entity.getType())
            .commodityLocation(entity.getCommodityLocation())
            .externalReference( uuidConverter.convertEntity(externalReference))
            .marketId(entity.getMarketId())
            .commodityName(commodityName)
            .buyPrice(entity.getBuyPrice())
            .sellPrice(entity.getSellPrice())
            .demand(entity.getDemand())
            .stock(entity.getStock())
            .averagePrice(commodityAveragePriceDao.findById(commodityName).map(CommodityAveragePrice::getAveragePrice).orElse(null))
            .build();
    }
}
