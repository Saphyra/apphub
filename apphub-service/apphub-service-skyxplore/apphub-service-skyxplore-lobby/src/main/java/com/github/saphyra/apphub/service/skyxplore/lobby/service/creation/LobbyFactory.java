package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LobbyFactory {
    private final DateTimeUtil dateTimeUtil;
    private final IdGenerator idGenerator;

    Lobby create(UUID userId) {
        return Lobby.builder()
            .lobbyId(idGenerator.randomUUID())
            .host(userId)
            .members(new ArrayList<>(Arrays.asList(userId)))
            .lastAccess(dateTimeUtil.getCurrentDate())
            .build();
    }
}
