package com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SatietyDecreaseService {
    private final GameProperties gameProperties;
    private final CitizenToModelConverter citizenToModelConverter;
    private final WsMessageSender messageSender;
    private final CitizenToResponseConverter citizenToResponseConverter;

    public void processGame(Game game, SyncCache syncCache) {
        game.getUniverse()
            .getSystems()
            .values()
            .stream()
            .flatMap(solarSystem -> solarSystem.getPlanets().values().stream())
            .forEach(planet -> processPlanet(game.getGameId(), planet, syncCache));
    }

    private void processPlanet(UUID gameId, Planet planet, SyncCache syncCache) {
        planet.getPopulation()
            .values()
            .forEach(citizen -> processCitizen(gameId, planet, citizen, syncCache));
    }

    private void processCitizen(UUID gameId, Planet planet, Citizen citizen, SyncCache syncCache) {
        log.debug("Decreasing satiety for citizen {}", citizen.getCitizenId());
        int satietyDecreasedPerSecond = gameProperties.getCitizen()
            .getSatiety()
            .getSatietyDecreasedPerSecond();
        citizen.setSatiety(citizen.getSatiety() - satietyDecreasedPerSecond);

        syncCache.saveGameItem(citizenToModelConverter.convert(citizen, gameId));
        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> messageSender.planetCitizenModified(planet.getOwner(), planet.getPlanetId(), citizenToResponseConverter.convert(citizen))
        );
    }
}
