package com.github.saphyra.apphub.service.custom.elite_base.dao.commodity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommodityCacheKey {
    private final Long marketId;
    private final CommodityType commodityType;
}
