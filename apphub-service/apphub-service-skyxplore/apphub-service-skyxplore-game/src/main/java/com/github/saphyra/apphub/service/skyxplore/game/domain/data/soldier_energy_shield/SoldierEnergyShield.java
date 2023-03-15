package com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_energy_shield;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SoldierEnergyShield {
    public static final String CITIZEN_ENERGY_SHIELD = "CITIZEN_ENERGY_SHIELD";

    private final UUID soldierEnergyShieldId;
    private final UUID citizenId;
    private final String dataId;
}
