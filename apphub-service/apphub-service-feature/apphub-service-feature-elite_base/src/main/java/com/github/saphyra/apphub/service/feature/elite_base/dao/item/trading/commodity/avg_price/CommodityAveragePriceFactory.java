package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommodityAveragePriceFactory {
    public CommodityAveragePrice create(String commodityName, Integer averagePrice) {
        return CommodityAveragePrice.builder()
            .commodityName(commodityName.toLowerCase())
            .averagePrice(averagePrice)
            .build();
    }
}
