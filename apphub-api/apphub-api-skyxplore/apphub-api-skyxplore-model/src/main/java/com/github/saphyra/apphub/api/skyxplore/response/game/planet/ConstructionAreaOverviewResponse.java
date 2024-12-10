package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ConstructionAreaOverviewResponse {
    private String dataId;
    private Map<String, BuildingModuleOverviewResponse> buildingModules; //Map<Slot, BuildingModuleOverviewResponse>
}
