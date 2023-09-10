package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.SkyXploreGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PauseGameService {
    private final GameDao gameDao;
    private final SkyXploreGameWebSocketHandler webSocketHandler;

    public void setPausedStatus(UUID userId, Boolean isPaused) {
        ValidationUtil.notNull(isPaused, "isPaused");

        Game game = gameDao.findByUserIdValidated(userId);
        game.setGamePaused(isPaused);

        webSocketHandler.sendEvent(game.getConnectedPlayers(), WebSocketEventName.SKYXPLORE_GAME_PAUSED, isPaused);
    }
}
