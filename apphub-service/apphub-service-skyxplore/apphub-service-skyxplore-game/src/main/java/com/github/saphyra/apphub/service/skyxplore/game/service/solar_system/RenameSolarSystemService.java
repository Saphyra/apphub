package com.github.saphyra.apphub.service.skyxplore.game.service.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystemConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class RenameSolarSystemService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final SolarSystemConverter solarSystemConverter;

    void rename(UUID userId, UUID solarSystemId, String newName) {
        if (isBlank(newName)) {
            throw ExceptionFactory.invalidParam("newName", "must not be null or blank");
        }

        if (newName.length() > 30) {
            throw ExceptionFactory.invalidParam("newName", "too long");
        }

        Game game = gameDao.findByUserIdValidated(userId);
        GameData gameData = game.getData();
        SolarSystem solarSystem = gameData.getSolarSystems()
            .findByIdValidated(solarSystemId);

        game.getEventLoop()
            .processWithWait(() -> {
                solarSystem.getCustomNames()
                    .put(userId, newName);

                SolarSystemModel model = solarSystemConverter.toModel(gameData.getGameId(), solarSystem);
                gameDataProxy.saveItem(model);
            })
            .getOrThrow();
    }
}
