package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurfaceBuildingResponse {
    private UUID buildingId;
    private String dataId;
    private int level;
    private ConstructionResponse construction;
    private DeconstructionResponse deconstruction;
}
