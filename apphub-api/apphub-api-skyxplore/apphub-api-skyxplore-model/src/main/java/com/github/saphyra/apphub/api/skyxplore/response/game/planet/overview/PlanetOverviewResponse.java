package com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.ConstructionAreaOverviewResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.SurfaceResponse;
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
    private Map<String, List<ConstructionAreaOverviewResponse>> buildingsSummary; //Map<SurfaceType, List<ConstructionAreasOnGivenSurfaceType>>
    private Map<String, Integer> priorities;
    private List<QueueResponse> queue;
}
