package com.github.saphyra.apphub.integration.structure.view.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PlanetBuildingOverviewItemDetails {
    private final Integer totalLevel;
    private final Integer usedSlots;
}
