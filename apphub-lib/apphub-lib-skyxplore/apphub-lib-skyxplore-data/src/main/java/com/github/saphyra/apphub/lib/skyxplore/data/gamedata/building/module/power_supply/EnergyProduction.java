package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EnergyProduction {
    private String id;
    private Integer energyPerBatch;
    private Integer batchTicks;
    @Builder.Default
    private Integer fuelLastsForTicks = 600;
    @Builder.Default
    private Boolean humanPowered = false;
    private ConstructionRequirements constructionRequirements;
}
