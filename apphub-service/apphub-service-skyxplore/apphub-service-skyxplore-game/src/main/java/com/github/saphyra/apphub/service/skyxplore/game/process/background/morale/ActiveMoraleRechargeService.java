package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.active.ActiveMoraleRechargeProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveMoraleRechargeService {
    private final GameProperties gameProperties;
    private final ActiveMoraleRechargeProcessFactory activeMoraleRechargeProcessFactory;

    public void processGame(Game game, SyncCache syncCache) {
        game.getUniverse()
            .getSystems()
            .values()
            .stream()
            .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
            .forEach(planet -> processPlanet(game, planet, syncCache));
    }

    private void processPlanet(Game game, Planet planet, SyncCache syncCache) {
        planet.getPopulation()
            .values()
            .forEach(citizen -> processCitizen(game, planet, citizen, syncCache));
    }

    private void processCitizen(Game game, Planet planet, Citizen citizen, SyncCache syncCache) {
        log.debug("Decreasing satiety for citizen {}", citizen.getCitizenId());
        if (citizen.getMorale() < gameProperties.getCitizen().getMorale().getMax() && !planet.getCitizenAllocations().containsKey(citizen.getCitizenId())) {
            Process process = activeMoraleRechargeProcessFactory.create(game, planet, citizen);
            syncCache.saveGameItem(process.toModel());
            game.getProcesses()
                .add(process);
        }
    }
}
