package com.github.saphyra.apphub.service.elite_base.message_processing.dao.commodity.avg_price;

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
