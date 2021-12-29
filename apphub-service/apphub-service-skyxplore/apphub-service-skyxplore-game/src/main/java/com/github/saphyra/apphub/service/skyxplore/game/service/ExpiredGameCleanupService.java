package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.CommonSkyXploreConfiguration;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ExpiredGameCleanupService {
    private final GameDao gameDao;
    private final DateTimeUtil dateTimeUtil;
    private final CommonSkyXploreConfiguration configuration;

    void cleanUp() {
        log.info("Cleaning up expired games...");
        gameDao.getAll()
            .stream()
            .filter(game -> game.getConnectedPlayers().isEmpty())
            .forEach(this::process);
    }

    private void process(Game game) {
        if (nonNull(game.getExpiresAt())) {
            if (dateTimeUtil.getCurrentDate().isAfter(game.getExpiresAt())) {
                log.info("Deleting game {}", game.getGameId());
                gameDao.delete(game);
            }
        } else {
            log.info("Marking game {} for deletion", game.getGameId());
            game.setExpiresAt(dateTimeUtil.getCurrentDate().plusSeconds(configuration.getAbandonedGameExpirationSeconds()));
        }
    }
}
