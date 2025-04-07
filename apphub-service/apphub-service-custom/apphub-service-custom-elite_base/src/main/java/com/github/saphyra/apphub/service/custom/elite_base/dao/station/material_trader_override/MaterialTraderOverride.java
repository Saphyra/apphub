package com.github.saphyra.apphub.service.custom.elite_base.dao.station.material_trader_override;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class MaterialTraderOverride {
    private final UUID stationId;
    private MaterialType materialType;
    private boolean verified;
}
