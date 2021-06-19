package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyCreationService {
    private final LobbyNameValidator lobbyNameValidator;
    private final ExitFromLobbyService exitFromLobbyService;
    private final LobbyDao lobbyDao;
    private final LobbyFactory lobbyFactory;

    public void create(UUID userId, String lobbyName) {
        lobbyNameValidator.validate(lobbyName);

        lobbyDao.findByUserId(userId)
            .ifPresent(lobby -> exitFromLobbyService.exit(userId));

        Lobby lobby = lobbyFactory.create(userId, lobbyName);
        lobbyDao.save(lobby);
    }
}
