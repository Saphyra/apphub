package com.github.saphyra.apphub.service.feature.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.feature.skyxplore.response.lobby.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.disconnect.ExitFromLobbyService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.player.LobbyPlayerQueryService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.start_game.GameLoadedService;
import com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.start_game.StartGameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreLobbyControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private ActiveFriendsService activeFriendsService;

    @Mock
    private ExitFromLobbyService exitFromLobbyService;

    @Mock
    private LobbyCreationService lobbyCreationService;

    @Mock
    private InvitationService invitationService;

    @Mock
    private JoinToLobbyService joinToLobbyService;

    @Mock
    private LobbyPlayerQueryService lobbyPlayerQueryService;

    @Mock
    private StartGameService startGameService;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private GameLoadedService gameLoadedService;

    @InjectMocks
    private SkyXploreLobbyControllerImpl underTest;

    @Mock
    private AccessToken accessToken;

    @Mock
    private ActiveFriendResponse activeFriendResponse;

    @Mock
    private LobbyPlayerResponse lobbyPlayerResponse;

    @Mock
    private Lobby lobby;

    @Test
    void lobbyForPage() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getLobbyName()).willReturn(LOBBY_NAME);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getType()).willReturn(LobbyType.LOAD_GAME);

        LobbyViewForPage result = underTest.lobbyForPage(accessToken);

        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.isHost()).isTrue();
        assertThat(result.getLobbyType()).isEqualTo(LobbyType.LOAD_GAME.name());
        assertThat(result.getOwnUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void createLobby() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.createLobby(new OneParamRequest<>(LOBBY_NAME), accessToken);

        verify(lobbyCreationService).createNew(USER_ID, LOBBY_NAME);
    }

    @Test
    public void exitFromLobby() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.exitFromLobby(accessToken);

        verify(exitFromLobbyService).exit(USER_ID);
    }

    @Test
    public void inviteToLobby() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.inviteToLobby(FRIEND_ID, accessToken);

        verify(invitationService).invite(accessToken, FRIEND_ID);
    }

    @Test
    public void acceptInvitation() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.acceptInvitation(FRIEND_ID, accessToken);

        verify(joinToLobbyService).acceptInvitation(USER_ID, FRIEND_ID);
    }

    @Test
    public void getPlayersOfLobby() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(lobbyPlayerQueryService.getPlayers(USER_ID)).willReturn(List.of(lobbyPlayerResponse));

        List<LobbyPlayerResponse> result = underTest.getPlayersOfLobby(accessToken);

        assertThat(result).containsExactly(lobbyPlayerResponse);
    }

    @Test
    public void startGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.startGame(accessToken);

        verify(startGameService).startGame(USER_ID);
    }

    @Test
    public void getActiveFriends() {
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(activeFriendsService.getActiveFriends(accessToken)).willReturn(Arrays.asList(activeFriendResponse));

        List<ActiveFriendResponse> result = underTest.getActiveFriends(accessToken);

        assertThat(result).containsExactly(activeFriendResponse);
    }

    @Test
    public void loadGame() {
        given(accessToken.getUserId()).willReturn(USER_ID);

        underTest.loadGame(GAME_ID, accessToken);

        verify(lobbyCreationService).createForExistingGame(USER_ID, GAME_ID);
    }

    @Test
    void gameLoaded() {
        underTest.gameLoaded(GAME_ID);

        then(gameLoadedService).should().gameLoaded(GAME_ID);
    }
}