package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.lobby.server.SkyXploreLobbyEventController;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@Builder
class SkyXploreLobbyEventControllerImpl implements SkyXploreLobbyEventController {
    private final LobbyDao lobbyDao;
    private final DateTimeUtil dateTimeUtil;
    private final int lobbyExpirationMinutes;

    public SkyXploreLobbyEventControllerImpl(
        LobbyDao lobbyDao,
        DateTimeUtil dateTimeUtil,
        @Value("${lobby.expirationMinutes}") int lobbyExpirationMinutes
    ) {
        this.lobbyDao = lobbyDao;
        this.dateTimeUtil = dateTimeUtil;
        this.lobbyExpirationMinutes = lobbyExpirationMinutes;
    }

    @Override
    public void cleanupExpiredLobbies() {
        log.info("Cleaning up expired lobbies...");
        LocalDateTime expiration = dateTimeUtil.getCurrentDate()
            .minusMinutes(lobbyExpirationMinutes);
        lobbyDao.getByLastAccessedBefore(expiration)
            .forEach(lobbyDao::delete);
        log.info("Expired lobbies are removed.");
    }
}
