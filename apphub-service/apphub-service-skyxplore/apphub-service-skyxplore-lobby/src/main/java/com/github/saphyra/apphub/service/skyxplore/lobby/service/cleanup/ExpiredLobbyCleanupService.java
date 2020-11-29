package com.github.saphyra.apphub.service.skyxplore.lobby.service.cleanup;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ExpiredLobbyCleanupService {
    private final LobbyDao lobbyDao;
    private final DateTimeUtil dateTimeUtil;

    @Value("${lobby.expirationMinutes}")
    private int lobbyExpirationMinutes;

    public void cleanup() {
        LocalDateTime expiration = dateTimeUtil.getCurrentDate()
            .minusMinutes(lobbyExpirationMinutes);
        lobbyDao.getByLastAccessedBefore(expiration)
            .forEach(lobbyDao::delete);
    }
}
