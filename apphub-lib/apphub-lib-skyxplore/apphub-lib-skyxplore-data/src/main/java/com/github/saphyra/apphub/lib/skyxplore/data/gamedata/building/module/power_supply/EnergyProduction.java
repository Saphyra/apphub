package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.power_supply;

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
    private Integer energyPerBatch; //Gives this amount of energy in every @BatchTicks
    private Integer batchTicks; //How often the building produces energy
    private Integer fuelLastsForTicks; //How often the building requires new fuel
    private Integer fuelStorage; //Building can store this amount of fuel for auto-refuel before it stops
    @Builder.Default
    private Boolean humanPowered = false;
    private String fuel;
}
