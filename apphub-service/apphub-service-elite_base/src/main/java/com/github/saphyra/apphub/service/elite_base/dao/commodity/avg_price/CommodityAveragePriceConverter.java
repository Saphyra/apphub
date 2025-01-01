package com.github.saphyra.apphub.service.elite_base.dao.commodity.avg_price;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommodityAveragePriceConverter extends ConverterBase<CommodityAveragePriceEntity, CommodityAveragePrice> {
    private final DateTimeConverter dateTimeConverter;

    @Override
    protected CommodityAveragePriceEntity processDomainConversion(CommodityAveragePrice domain) {
        return CommodityAveragePriceEntity.builder()
            .commodityName(domain.getCommodityName())
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .averagePrice(domain.getAveragePrice())
            .build();
    }

    @Override
    protected CommodityAveragePrice processEntityConversion(CommodityAveragePriceEntity entity) {
        return CommodityAveragePrice.builder()
            .commodityName(entity.getCommodityName())
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .averagePrice(entity.getAveragePrice())
            .build();
    }
}
