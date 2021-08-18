package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ExpiredGameCleanupService {
    private final GameDao gameDao;

    void cleanUp() {
        log.info("Cleaning up expired games...");
        gameDao.getAll()
            .stream()
            .filter(game -> game.getConnectedPlayers().isEmpty())
            .forEach(this::process);
    }

    private void process(Game game) {
        if (game.isMarkedForDeletion()) {
            log.info("Deleting game {}", game.getGameId());
            gameDao.delete(game);
        } else {
            log.info("Marking game {} for deletion", game.getGameId());
            game.setMarkedForDeletion(true);
        }
    }
}
