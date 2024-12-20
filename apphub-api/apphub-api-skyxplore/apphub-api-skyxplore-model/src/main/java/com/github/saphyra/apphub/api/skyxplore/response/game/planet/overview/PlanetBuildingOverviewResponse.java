package com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Deprecated(forRemoval = true)
public class PlanetBuildingOverviewResponse {
    private List<PlanetBuildingOverviewDetailedResponse> buildingDetails;
    private int slots;
    private int usedSlots;
}
