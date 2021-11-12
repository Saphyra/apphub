package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ConcurrentMap;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SoldierData {
    public static final String CITIZEN_HIT_POINTS = "CITIZEN_HIT_POINTS";

    private int maxHitPoints;
    private int currentHitPoints;
    private final ConcurrentMap<BodyPart, SoldierArmorPiece> armor;
    private SoldierEnergyShield energyShield;
    private String weaponDataId;
    private String meleeWeaponDataId;
}
