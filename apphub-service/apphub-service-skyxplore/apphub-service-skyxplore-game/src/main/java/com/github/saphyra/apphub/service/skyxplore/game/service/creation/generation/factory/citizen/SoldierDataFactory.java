package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.citizen;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
class SoldierDataFactory {
    private final GameProperties properties;

    SoldierWeapon create() {
        int hitPoints = properties.getCitizen()
            .getHitPoints()
            .getPerStaminaLevel();
        return SoldierWeapon.builder()
            .maxHitPoints(hitPoints)
            .currentHitPoints(hitPoints)
            .armor(new ConcurrentHashMap<>())
            .energyShield(null)
            .weaponDataId(null)
            .meleeWeaponDataId(null)
            .build();
    }
}
