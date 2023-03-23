package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.active.ActiveMoraleRechargeProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveMoraleRechargeService {
    private final GameProperties gameProperties;
    private final ActiveMoraleRechargeProcessFactory activeMoraleRechargeProcessFactory;

    public void processGame(GameData gameData, SyncCache syncCache) {
        gameData.getPlanets()
            .forEach((planetId, planet) -> processPlanet(gameData, planetId, syncCache));
    }

    private void processPlanet(GameData gameData, UUID location, SyncCache syncCache) {
        gameData.getCitizens()
            .getByLocation(location)
            .forEach(citizen -> processCitizen(gameData, location, citizen, syncCache));
    }

    private void processCitizen(GameData gameData, UUID location, Citizen citizen, SyncCache syncCache) {
        log.debug("Decreasing satiety for citizen {}", citizen.getCitizenId());
        if (citizen.getMorale() < gameProperties.getCitizen().getMorale().getMax() && gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty()) {
            Process process = activeMoraleRechargeProcessFactory.create(gameData, location, citizen);
            syncCache.saveGameItem(process.toModel());
            gameData.getProcesses()
                .add(process);
        }
    }
}
