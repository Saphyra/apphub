package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.api.skyxplore.game.server.platform.SkyXploreGameController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickSchedulerLauncher;
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
    private final SaveGameService saveGameService;
    private final TickSchedulerLauncher tickSchedulerLauncher;

    @Override
    public OneParamResponse<UUID> getGameId(AccessTokenHeader accessTokenHeader) {
        log.info("Checking if user {} is in game.", accessTokenHeader.getUserId());
        UUID result = gameDao.findByUserId(accessTokenHeader.getUserId())
            .map(Game::getGameId)
            .orElse(null);
        return new OneParamResponse<>(result);
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

    @Override
    public OneParamResponse<Boolean> isHost(AccessTokenHeader accessTokenHeader) {
        log.info("Checking if {} is game host.", accessTokenHeader.getUserId());
        boolean isHost = gameDao.findByUserIdValidated(accessTokenHeader.getUserId())
            .getHost()
            .equals(accessTokenHeader.getUserId());
        return new OneParamResponse<>(isHost);
    }

    @Override
    public void saveGame(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to save game.", accessTokenHeader.getUserId());
        saveGameService.saveGame(accessTokenHeader.getUserId());
    }

    @Override
    public void processTick(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to process a tick of their game.", accessTokenHeader.getUserId());

        tickSchedulerLauncher.processTick(accessTokenHeader.getUserId());
    }
}
