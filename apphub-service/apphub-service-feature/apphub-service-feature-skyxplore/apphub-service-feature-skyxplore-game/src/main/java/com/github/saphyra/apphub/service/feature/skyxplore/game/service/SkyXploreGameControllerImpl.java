package com.github.saphyra.apphub.service.feature.skyxplore.game.service;

import com.github.saphyra.apphub.api.feature.skyxplore.game.server.platform.SkyXploreGameController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.feature.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.feature.skyxplore.game.simulation.tick.TickSchedulerLauncher;
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
    public OneParamResponse<UUID> getGameId(AccessToken accessToken) {
        log.info("Checking if user {} is in game.", accessToken.getUserId());
        UUID result = gameDao.findByUserId(accessToken.getUserId())
            .map(Game::getGameId)
            .orElse(null);
        return new OneParamResponse<>(result);
    }

    @Override
    public void cleanUpExpiredGames() {
        expiredGameCleanupService.cleanUp();
    }

    @Override
    public void exitGame(AccessToken accessToken) {
        log.info("{} wants to leave the game.", accessToken.getUserId());
        exitFromGameService.exitFromGame(accessToken.getUserId());
    }

    @Override
    public void pauseGame(OneParamRequest<Boolean> paused, AccessToken accessToken) {
        log.info("{} wants to pause game: {}", accessToken.getUserId(), paused.getValue());
        pauseGameService.setPausedStatus(accessToken.getUserId(), paused.getValue());
    }

    @Override
    public void deleteGame(UUID gameId) {
        log.info("Deleting game {}", gameId);

        gameDao.delete(gameId);
    }

    @Override
    public OneParamResponse<Boolean> isHost(AccessToken accessToken) {
        log.info("Checking if {} is game host.", accessToken.getUserId());
        boolean isHost = gameDao.findByUserIdValidated(accessToken.getUserId())
            .getHost()
            .equals(accessToken.getUserId());
        return new OneParamResponse<>(isHost);
    }

    @Override
    public void saveGame(AccessToken accessToken) {
        log.info("{} wants to save game.", accessToken.getUserId());
        saveGameService.saveGame(accessToken.getUserId());
    }

    @Override
    public void processTick(AccessToken accessToken) {
        log.info("{} wants to process a tick of their game.", accessToken.getUserId());

        tickSchedulerLauncher.processTick(accessToken.getUserId());
    }
}
