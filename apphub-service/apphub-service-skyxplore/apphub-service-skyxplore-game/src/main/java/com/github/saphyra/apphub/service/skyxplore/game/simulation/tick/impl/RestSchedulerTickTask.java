package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
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
class RestSchedulerTickTask implements TickTask {
    private final GameProperties gameProperties;
    private final RestProcessFactory restProcessFactory;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.REST_SCHEDULER;
    }

    @Override
    public void process(Game game) {
        game.getData()
            .getCitizens()
            .stream()
            .filter(citizen -> game.getData().getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty())
            .filter(citizen -> citizen.getMorale() < gameProperties.getCitizen().getMorale().getRestingMoraleLimit())
            .forEach(citizen -> scheduleRestIfNeeded(game, game.getData(), citizen));
    }

    private void scheduleRestIfNeeded(Game game, GameData gameData, Citizen citizen) {
        CitizenMoraleProperties moraleProperties = gameProperties.getCitizen()
            .getMorale();

        int restForTicks = citizen.getMorale() > moraleProperties.getExhaustedMorale() ? moraleProperties.getMinRestTicks() : moraleProperties.getExhaustedRestTicks();

        RestProcess restProcess = restProcessFactory.create(game, citizen, restForTicks);
        gameData.getProcesses()
            .add(restProcess);
        game.getProgressDiff()
            .save(restProcess.toModel());
    }
}
