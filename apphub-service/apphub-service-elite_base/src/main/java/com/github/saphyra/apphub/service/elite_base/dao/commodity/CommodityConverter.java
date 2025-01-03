package com.github.saphyra.apphub.service.elite_base.dao.commodity;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.avg_price.CommodityAveragePrice;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.avg_price.CommodityAveragePriceDao;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.avg_price.CommodityAveragePriceFactory;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update.CommodityLastUpdate;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update.CommodityLastUpdateDao;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.last_update.CommodityLastUpdateId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommodityConverter extends ConverterBase<CommodityEntity, Commodity> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final CommodityAveragePriceDao commodityAveragePriceDao;
    private final CommodityAveragePriceFactory commodityAveragePriceFactory;
    private final CommodityLastUpdateDao commodityLastUpdateDao;

    @Override
    protected CommodityEntity processDomainConversion(Commodity domain) {
        commodityAveragePriceDao.save(commodityAveragePriceFactory.create(domain.getLastUpdate(), domain.getCommodityName(), domain.getAveragePrice()));

        return CommodityEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .type(domain.getType())
            .commodityLocation(domain.getCommodityLocation())
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .marketId(domain.getMarketId())
            .commodityName(domain.getCommodityName())
            .buyPrice(domain.getBuyPrice())
            .sellPrice(domain.getSellPrice())
            .demand(domain.getDemand())
            .stock(domain.getStock())
            .build();
    }

    @Override
    protected Commodity processEntityConversion(CommodityEntity entity) {
        UUID externalReference = uuidConverter.convertEntity(entity.getExternalReference());
        return Commodity.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .lastUpdate(commodityLastUpdateDao.findById(CommodityLastUpdateId.builder()
                    .externalReference(entity.getExternalReference())
                    .commodityType(entity.getType())
                    .build())
                .map(CommodityLastUpdate::getLastUpdate)
                .orElse(null))
            .type(entity.getType())
            .commodityLocation(entity.getCommodityLocation())
            .externalReference(externalReference)
            .marketId(entity.getMarketId())
            .commodityName(entity.getCommodityName())
            .buyPrice(entity.getBuyPrice())
            .sellPrice(entity.getSellPrice())
            .demand(entity.getDemand())
            .stock(entity.getStock())
            .averagePrice(commodityAveragePriceDao.findById(entity.getCommodityName()).map(CommodityAveragePrice::getAveragePrice).orElse(null))
            .build();
    }
}
