package com.github.saphyra.apphub.service.feature.elite_base.dao.item.trading.commodity.avg_price;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CommodityAveragePrice {
    private final String commodityName;
    private Integer averagePrice;
}
