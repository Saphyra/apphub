package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.main.SkyXploreGameMainWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PauseGameService {
    private final GameDao gameDao;
    private final SkyXploreGameMainWebSocketHandler webSocketHandler;

    public void setPausedStatus(UUID userId, Boolean isPaused) {
        ValidationUtil.notNull(isPaused, "isPaused");

        Game game = gameDao.findByUserIdValidated(userId);

        if (!game.getHost().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " is not the host of game " + game.getGameId());
        }

        setPausedStatus(isPaused, game);

        game.getPlayers()
            .forEach((uuid, player) -> player.setDisconnectedAt(null));
    }

    public void setPausedStatus(Boolean isPaused, Game game) {
        game.setGamePaused(isPaused);

        webSocketHandler.sendEvent(game.getConnectedPlayers(), WebSocketEventName.SKYXPLORE_GAME_PAUSED, isPaused);
    }
}
