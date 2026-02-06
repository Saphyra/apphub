package com.github.saphyra.apphub.api.custom.elite_base.model.material_trader;

import com.github.saphyra.apphub.api.custom.elite_base.model.MaterialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateMaterialTraderOverrideRequest {
    private UUID stationId;
    private MaterialType materialType;
}
