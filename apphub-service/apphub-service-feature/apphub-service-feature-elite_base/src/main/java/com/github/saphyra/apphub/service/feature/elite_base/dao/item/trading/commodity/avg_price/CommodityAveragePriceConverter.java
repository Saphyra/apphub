package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CommodityAveragePriceConverter extends ConverterBase<CommodityAveragePriceEntity, CommodityAveragePrice> {
    @Override
    protected CommodityAveragePriceEntity processDomainConversion(CommodityAveragePrice domain) {
        return CommodityAveragePriceEntity.builder()
            .commodityName(domain.getCommodityName())
            .averagePrice(domain.getAveragePrice())
            .build();
    }

    @Override
    protected CommodityAveragePrice processEntityConversion(CommodityAveragePriceEntity entity) {
        return CommodityAveragePrice.builder()
            .commodityName(entity.getCommodityName())
            .averagePrice(entity.getAveragePrice())
            .build();
    }
}
