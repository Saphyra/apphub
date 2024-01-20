package com.github.saphyra.apphub.service.skyxplore.game.service.planet;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.PlanetConverter;
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
    private final PlanetConverter planetConverter;

    //TODO use eventLoop
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

        game.getEventLoop()
            .processWithWait(() -> {
                planet.getCustomNames()
                    .put(userId, newName);

                game.getProgressDiff()
                    .save(planetConverter.toModel(game.getGameId(), planet));
            })
            .getOrThrow();
    }
}
