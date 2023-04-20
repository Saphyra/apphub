package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest.RestProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest.RestProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class RestSchedulerTickTask implements TickTask {
    private final GameProperties gameProperties;
    private final RestProcessFactory restProcessFactory;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.REST_SCHEDULER;
    }

    @Override
    public void process(Game game, SyncCache syncCache) {
        game.getData()
            .getCitizens()
            .stream()
            .filter(citizen -> game.getData().getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty())
            .forEach(citizen -> scheduleRestIfNeeded(syncCache, game.getData(), citizen));
    }

    private void scheduleRestIfNeeded(SyncCache syncCache, GameData gameData, Citizen citizen) {
        if (gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isPresent()) {
            log.info("{} is working.", citizen.getCitizenId());
            return;
        }

        CitizenMoraleProperties moraleProperties = gameProperties.getCitizen().getMorale();
        if (citizen.getMorale() > moraleProperties.getRestingMoraleLimit()) {
            log.info("{} has {} morale, and no need to rest.", citizen.getCitizenId(), citizen.getMorale());
            return;
        }

        int restForTicks = citizen.getMorale() > moraleProperties.getExhaustedMorale() ? moraleProperties.getMinRestTicks() : moraleProperties.getExhaustedRestTicks();

        RestProcess restProcess = restProcessFactory.create(gameData, citizen, restForTicks);
        gameData.getProcesses()
            .add(restProcess);
        syncCache.saveGameItem(restProcess
            .toModel());
    }
}