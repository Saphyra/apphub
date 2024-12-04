package com.github.saphyra.apphub.integration.structure.api.skyxplore.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BuildingModuleOverviewResponse {
    private Integer availableSlots;
    private Integer usedSlots;
    private Map<String, Integer> modules; //Map<buildingModuleDataId, numberOfModules>
 }
