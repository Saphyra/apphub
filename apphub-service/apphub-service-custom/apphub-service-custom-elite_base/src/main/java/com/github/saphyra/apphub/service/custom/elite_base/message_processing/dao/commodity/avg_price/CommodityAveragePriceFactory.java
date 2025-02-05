package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity.avg_price;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommodityAveragePriceFactory {
    public CommodityAveragePrice create(LocalDateTime lastUpdate, String commodityName, Integer averagePrice) {
        return CommodityAveragePrice.builder()
            .commodityName(commodityName.toLowerCase())
            .lastUpdate(lastUpdate)
            .averagePrice(averagePrice)
            .build();
    }
}
