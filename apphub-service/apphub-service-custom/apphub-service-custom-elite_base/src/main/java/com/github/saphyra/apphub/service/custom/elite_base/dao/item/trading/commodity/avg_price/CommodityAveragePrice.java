package com.github.saphyra.apphub.service.custom.elite_base.dao.item.trading.commodity.avg_price;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommodityAveragePrice {
    private final String commodityName;
    private LocalDateTime lastUpdate;
    private Integer averagePrice;
}
