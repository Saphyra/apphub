package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

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
    private Map<String, PlanetBuildingOverviewResponse> buildings;
    private Map<String, Integer> priorities;
    private List<QueueResponse> queue;
}
