package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
class SoldierDataFactory {
    private final GameProperties properties;

    SoldierData create() {
        int hitPoints = properties.getCitizen()
            .getHitPoints()
            .getPerStaminaLevel();
        return SoldierData.builder()
            .maxHitPoints(hitPoints)
            .currentHitPoints(hitPoints)
            .armor(new ConcurrentHashMap<>())
            .energyShield(null)
            .weaponDataId(null)
            .meleeWeaponDataId(null)
            .build();
    }
}
