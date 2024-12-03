package com.github.saphyra.apphub.integration.structure.api.skyxplore.game.building;

import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.ConstructionResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.DeconstructionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BuildingModuleResponse {
    private UUID buildingModuleId;
    private String dataId;
    private String buildingModuleCategory;
    private ConstructionResponse construction;
    private DeconstructionResponse deconstruction;
    private Map<String, Object> data;
}
