package com.github.saphyra.apphub.service.skyxplore.game.service;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.ws.SkyXploreGameWebSocketEventControllerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ExitFromGameService {
    private final SkyXploreGameWebSocketEventControllerImpl wsEventController;
    private final GameDao gameDao;

    public void exitFromGame(UUID userId) {
        wsEventController.userLeftGame(userId);
        gameDao.findByUserId(userId)
            .ifPresent(gameDao::delete);
    }
}
