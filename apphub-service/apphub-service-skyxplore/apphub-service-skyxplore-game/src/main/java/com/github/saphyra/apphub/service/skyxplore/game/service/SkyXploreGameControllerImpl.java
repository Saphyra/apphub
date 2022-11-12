package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.api.skyxplore.game.server.SkyXploreGameController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SkyXploreGameControllerImpl implements SkyXploreGameController {
    private final GameDao gameDao;
    private final ExpiredGameCleanupService expiredGameCleanupService;
    private final ExitFromGameService exitFromGameService;
    private final PauseGameService pauseGameService;

    @Override
    public boolean isUserInGame(AccessTokenHeader accessTokenHeader) {
        log.info("Checking if user {} is in game.", accessTokenHeader.getUserId());
        return gameDao.findByUserId(accessTokenHeader.getUserId()).isPresent();
    }

    @Override
    public void cleanUpExpiredGames() {
        expiredGameCleanupService.cleanUp();
    }

    @Override
    public void exitGame(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to leave the game.", accessTokenHeader.getUserId());
        exitFromGameService.exitFromGame(accessTokenHeader.getUserId());
    }

    @Override
    public void pauseGame(OneParamRequest<Boolean> paused, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to pause game: {}", accessTokenHeader.getUserId(), paused.getValue());
        pauseGameService.setPausedStatus(accessTokenHeader.getUserId(), paused.getValue());
    }

    @Override
    public void deleteGame(UUID gameId) {
        log.info("Deleting game {}", gameId);

        gameDao.delete(gameId);
    }
}
