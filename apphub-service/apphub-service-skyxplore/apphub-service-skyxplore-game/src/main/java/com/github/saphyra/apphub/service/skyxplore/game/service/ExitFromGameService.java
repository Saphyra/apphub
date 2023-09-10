package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.ws.handler.SkyXploreGameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ExitFromGameService {
    private final SkyXploreGameWebSocketHandler gameWebSocketHandler;
    private final GameDao gameDao;

    public void exitFromGame(UUID userId) {
        gameWebSocketHandler.afterDisconnection(userId);
        gameDao.findByUserId(userId)
            .ifPresent(gameDao::delete); //Removal is necessary to let the user logging in to another game instantly
    }
}
