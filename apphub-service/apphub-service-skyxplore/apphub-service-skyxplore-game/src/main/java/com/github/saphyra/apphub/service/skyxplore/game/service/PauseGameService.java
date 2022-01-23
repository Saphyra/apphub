package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WebSocketMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class PauseGameService {
    private final GameDao gameDao;
    private final WebSocketMessageFactory messageFactory;
    private final MessageSenderProxy messageSenderProxy;

    public void setPausedStatus(UUID userId, Boolean isPaused) {
        ValidationUtil.notNull(isPaused, "isPaused");

        Game game = gameDao.findByUserIdValidated(userId);
        game.setGamePaused(isPaused);

        messageSenderProxy.sendToGame(messageFactory.create(game.getConnectedPlayers(), WebSocketEventName.SKYXPLORE_GAME_PAUSED, isPaused));
    }
}
