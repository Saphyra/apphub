package com.github.saphyra.apphub.service.skyxplore.game.process.background.satiety;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
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
public class SatietyDecreaseService {
    private final GameProperties gameProperties;
    private final CitizenToModelConverter citizenToModelConverter;
    private final WsMessageSender messageSender;
    private final CitizenToResponseConverter citizenToResponseConverter;

    public void processGame(GameData gameData, SyncCache syncCache) {
        gameData.getCitizens()
            .forEach(citizen -> processCitizen(gameData, citizen, syncCache));
    }

    private void processCitizen(GameData gameData, Citizen citizen, SyncCache syncCache) {
        log.debug("Decreasing satiety for citizen {}", citizen.getCitizenId());
        int satietyDecreasedPerSecond = gameProperties.getCitizen()
            .getSatiety()
            .getSatietyDecreasedPerSecond();
        citizen.setSatiety(citizen.getSatiety() - satietyDecreasedPerSecond);

        UUID ownerId = gameData.getPlanets()
            .get(citizen.getLocation())
            .getOwner();

        syncCache.saveGameItem(citizenToModelConverter.convert(gameData.getGameId(), citizen));
        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> messageSender.planetCitizenModified(
                ownerId,
                citizen.getLocation(),
                citizenToResponseConverter.convert(gameData, citizen)
            )
        );
    }
}
