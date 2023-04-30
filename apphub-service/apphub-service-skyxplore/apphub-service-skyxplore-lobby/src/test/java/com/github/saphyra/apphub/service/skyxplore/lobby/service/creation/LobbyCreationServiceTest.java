package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.GameViewForLobbyCreation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LobbyCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private ExitFromLobbyService exitFromLobbyService;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private LobbyFactory lobbyFactory;

    @Mock
    private LobbyNameValidator lobbyNameValidator;

    @Mock
    private SkyXploreDataProxy skyXploreDataProxy;

    @Mock
    private InvitationService invitationService;

    @InjectMocks
    private LobbyCreationService underTest;

    @Mock
    private Lobby currentLobby;

    @Mock
    private Lobby newLobby;

    @Mock
    private GameViewForLobbyCreation gameViewForLobbyCreation;

    @Mock
    private PlayerModel playerModel;

    @Test
    public void create() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(currentLobby));
        given(lobbyFactory.createForNewGame(USER_ID, LOBBY_NAME)).willReturn(newLobby);

        underTest.createNew(USER_ID, LOBBY_NAME);

        verify(lobbyNameValidator).validate(LOBBY_NAME);
        verify(exitFromLobbyService).exit(USER_ID, currentLobby);
        verify(lobbyDao).save(newLobby);
    }

    @Test
    public void createForExistingGame() {
        given(skyXploreDataProxy.getGameForLobbyCreation(GAME_ID)).willReturn(gameViewForLobbyCreation);
        given(gameViewForLobbyCreation.getPlayers()).willReturn(Arrays.asList(playerModel));
        given(playerModel.getUserId()).willReturn(PLAYER_ID);

        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(currentLobby));

        given(lobbyFactory.createForLoadGame(USER_ID, GAME_ID, gameViewForLobbyCreation)).willReturn(newLobby);

        underTest.createForExistingGame(USER_ID, GAME_ID);

        verify(exitFromLobbyService).exit(USER_ID, currentLobby);
        verify(lobbyDao).save(newLobby);
        verify(invitationService).inviteDirectly(USER_ID, PLAYER_ID, newLobby);
    }
}