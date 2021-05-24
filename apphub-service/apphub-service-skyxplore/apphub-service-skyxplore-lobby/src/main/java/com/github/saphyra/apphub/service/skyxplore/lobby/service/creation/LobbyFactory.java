package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
class LobbyFactory {
    private final DateTimeUtil dateTimeUtil;
    private final IdGenerator idGenerator;

    Lobby create(UUID userId, String lobbyName) {
        Map<UUID, Member> members = new ConcurrentHashMap<>();
        members.put(userId, Member.builder().userId(userId).build());

        return Lobby.builder()
            .lobbyId(idGenerator.randomUuid())
            .lobbyName(lobbyName)
            .host(userId)
            .members(members)
            .lastAccess(dateTimeUtil.getCurrentDate())
            .build();
    }
}
