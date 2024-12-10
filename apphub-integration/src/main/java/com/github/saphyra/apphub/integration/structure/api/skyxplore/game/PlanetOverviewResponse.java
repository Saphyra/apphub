package com.github.saphyra.apphub.integration.structure.api.skyxplore.game;

import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlanetOverviewResponse {
    private String planetName;
    private List<SurfaceResponse> surfaces;
    private PlanetStorageResponse storage;
    private PlanetPopulationOverviewResponse population;
    @Deprecated(forRemoval = true)
    private Map<String, PlanetBuildingOverviewResponse> buildings;
    private Map<String, List<ConstructionAreaOverviewResponse>> buildingsSummary; //Map<SurfaceType, List<ConstructionAreasOnGivenSurfaceType>>
    private Map<String, Integer> priorities;
    private List<QueueResponse> queue;
}