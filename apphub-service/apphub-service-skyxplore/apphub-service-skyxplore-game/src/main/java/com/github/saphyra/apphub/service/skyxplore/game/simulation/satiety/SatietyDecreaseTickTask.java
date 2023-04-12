package com.github.saphyra.apphub.service.skyxplore.game.simulation.satiety;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTask;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SatietyDecreaseTickTask implements TickTask {
    private final GameProperties gameProperties;
    private final WsMessageSender messageSender;
    private final CitizenConverter citizenConverter;

    @Override
    public TickTaskOrder getOrder() {
        return TickTaskOrder.SATIETY_DECREASE;
    }

    @Override
    public void process(Game game, SyncCache syncCache) {
        game.getData()
            .getCitizens()
            .stream()
            .collect(Collectors.groupingBy(Citizen::getLocation))
            .forEach((location, citizens) -> processCitizens(syncCache, game.getData(), location, citizens));
    }

    private void processCitizens(SyncCache syncCache, GameData gameData, UUID location, List<Citizen> citizens){
        citizens.forEach(citizen -> processCitizen(syncCache, gameData, citizen));

        UUID userId = gameData.getPlanets()
            .get(location)
            .getOwner();

        messageSender.planetCitizenModified(userId, location, citizenConverter.toResponse(gameData, citizens));
    }

    private void processCitizen(SyncCache syncCache, GameData gameData, Citizen citizen) {
        int decrease = gameProperties.getCitizen()
            .getSatiety()
            .getSatietyDecreasedPerTick();

        citizen.decreaseSatiety(decrease);

        syncCache.saveGameItem(citizenConverter.toModel(gameData.getGameId(), citizen));
    }
}
