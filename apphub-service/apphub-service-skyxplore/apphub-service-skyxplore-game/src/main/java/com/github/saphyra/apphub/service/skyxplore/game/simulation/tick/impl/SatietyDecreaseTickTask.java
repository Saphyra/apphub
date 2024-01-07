package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SatietyDecreaseTickTask implements TickTask {
    private final GameProperties gameProperties;
    private final CitizenConverter citizenConverter;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.SATIETY_DECREASE;
    }

    @Override
    public void process(Game game, SyncCache syncCache) {
        log.info("Decreasing satiety of citizens in game {}", game.getGameId());

        game.getData()
            .getCitizens()
            .forEach(citizen -> processCitizen(syncCache, game.getGameId(), citizen));
    }

    private void processCitizen(SyncCache syncCache, UUID gameId, Citizen citizen) {
        int decrease = gameProperties.getCitizen()
            .getSatiety()
            .getSatietyDecreasedPerTick();

        citizen.decreaseSatiety(decrease);

        syncCache.saveGameItem(citizenConverter.toModel(gameId, citizen));
    }
}
