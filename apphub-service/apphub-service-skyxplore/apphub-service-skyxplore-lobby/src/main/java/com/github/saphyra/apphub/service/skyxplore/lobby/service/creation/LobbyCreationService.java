package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

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
public class LobbyCreationService {
    private final LobbyDao lobbyDao;
    private final LobbyFactory lobbyFactory;

    public void createIfNotExists(UUID userId) {
        if (lobbyDao.alreadyInLobby(userId)) {
            log.info("{} is already in lobby.", userId);
            return;
        }
        log.info("{} is not in lobby. Creating a new one...", userId);

        Lobby lobby = lobbyFactory.create(userId);
        lobbyDao.save(lobby);
    }
}
