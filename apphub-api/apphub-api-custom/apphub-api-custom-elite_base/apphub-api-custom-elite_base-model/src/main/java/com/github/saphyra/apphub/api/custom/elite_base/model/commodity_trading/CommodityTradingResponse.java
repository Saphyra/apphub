package com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommodityTradingResponse {
    private Integer offset;
    private List<CommodityTradingResponseItem> items;
}
