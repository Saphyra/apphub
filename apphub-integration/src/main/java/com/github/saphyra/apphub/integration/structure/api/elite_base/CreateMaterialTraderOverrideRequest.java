package com.github.saphyra.apphub.integration.structure.api.elite_base;

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
