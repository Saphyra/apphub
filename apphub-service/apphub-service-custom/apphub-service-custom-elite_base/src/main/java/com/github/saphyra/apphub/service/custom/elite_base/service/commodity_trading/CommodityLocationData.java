package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading;

import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class CommodityLocationData {
    private final UUID externalReference;
    private final UUID starSystemId;
    private final UUID bodyId;
    private final String locationName;
    @Builder.Default
    private final StationType stationType = StationType.FLEET_CARRIER;
}
