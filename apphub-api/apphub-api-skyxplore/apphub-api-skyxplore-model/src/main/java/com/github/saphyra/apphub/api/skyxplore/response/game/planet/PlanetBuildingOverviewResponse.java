package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlanetBuildingOverviewResponse {
    private List<PlanetBuildingOverviewDetailedResponse> buildingDetails;
    private int slots;
    private int usedSlots;
}
