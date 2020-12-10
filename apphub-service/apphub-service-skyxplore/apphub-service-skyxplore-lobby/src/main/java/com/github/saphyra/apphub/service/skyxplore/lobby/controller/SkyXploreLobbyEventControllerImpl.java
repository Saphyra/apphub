package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyEventController;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.cleanup.ExpiredLobbyCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreLobbyEventControllerImpl implements SkyXploreLobbyEventController {
    private final ExpiredLobbyCleanupService expiredLobbyCleanupService;

    @Override
    //TODO unit test
    //TODO int test
    public void cleanupExpiredLobbies() {
        log.info("Cleaning up expired lobbies...");
        expiredLobbyCleanupService.cleanup();
        log.info("Expired lobbies are removed.");
    }
}
