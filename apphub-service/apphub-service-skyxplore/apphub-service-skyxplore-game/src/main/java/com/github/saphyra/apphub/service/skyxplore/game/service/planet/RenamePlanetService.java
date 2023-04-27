package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.PlanetToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
@Slf4j
class RenamePlanetService {
    private final GameDao gameDao;
    private final GameDataProxy gameDataProxy;
    private final PlanetToModelConverter planetToModelConverter;

    void rename(UUID userId, UUID planetId, String newName) {
        if (isBlank(newName)) {
            throw ExceptionFactory.invalidParam("newName", "must not be null or blank");
        }

        if (newName.length() > 20) {
            throw ExceptionFactory.invalidParam("newName", "too long");
        }

        Game game = gameDao.findByUserIdValidated(userId);
        Planet planet = game.getData()
            .getPlanets()
            .get(planetId);

        planet.getCustomNames()
            .put(userId, newName);

        PlanetModel model = planetToModelConverter.convert(game.getGameId(), planet);
        gameDataProxy.saveItem(model);
    }
}
