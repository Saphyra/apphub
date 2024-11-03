package com.github.saphyra.apphub.api.skyxplore.response.game.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Deprecated(forRemoval = true)
public class PlanetBuildingOverviewDetailedResponse {
    private String dataId;
    private int levelSum;
    private int usedSlots;
}
