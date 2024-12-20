package com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanetPopulationOverviewResponse {
    private int population;
    private int capacity;
}
