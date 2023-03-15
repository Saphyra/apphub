package com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SoldierWeapon {
    private final UUID soldierWeaponId;
    private String rangedWeaponDataId;
    private String meleeWeaponDataId;
}
