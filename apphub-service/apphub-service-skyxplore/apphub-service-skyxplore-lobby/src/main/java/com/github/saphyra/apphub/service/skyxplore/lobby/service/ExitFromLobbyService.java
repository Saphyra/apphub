package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ExitFromLobbyService {
    private final LobbyDao lobbyDao;

    public void exit(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        lobby.getMembers().remove(userId);

        //TODO Notify members about someone left the room

        if (lobby.getHost().equals(userId)) {
            //TODO notify members about the host left the room
        }
    }
}
