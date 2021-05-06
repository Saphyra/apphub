package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LobbyCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";

    @Mock
    private ExitFromLobbyService exitFromLobbyService;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private LobbyFactory lobbyFactory;

    @InjectMocks
    private LobbyCreationService underTest;

    @Mock
    private Lobby currentLobby;

    @Mock
    private Lobby newLobby;

    @Test
    public void create() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(currentLobby));
        given(lobbyFactory.create(USER_ID, LOBBY_NAME)).willReturn(newLobby);

        underTest.create(USER_ID, LOBBY_NAME);

        verify(exitFromLobbyService).exit(USER_ID);
        verify(lobbyDao).save(newLobby);
    }
}