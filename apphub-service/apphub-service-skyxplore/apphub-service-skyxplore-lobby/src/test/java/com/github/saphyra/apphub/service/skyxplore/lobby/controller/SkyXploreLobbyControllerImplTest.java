package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberQueryService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game.GameLoadedService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game.StartGameService;
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
    private LobbyMemberQueryService lobbyMemberQueryService;

    @Mock
    private StartGameService startGameService;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private GameLoadedService gameLoadedService;

    @InjectMocks
    private SkyXploreLobbyControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private ActiveFriendResponse activeFriendResponse;

    @Mock
    private LobbyMemberResponse lobbyMemberResponse;

    @Mock
    private Lobby lobby;

    @Test
    void lobbyForPage() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getLobbyName()).willReturn(LOBBY_NAME);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getType()).willReturn(LobbyType.LOAD_GAME);

        LobbyViewForPage result = underTest.lobbyForPage(accessTokenHeader);

        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.isHost()).isTrue();
        assertThat(result.getLobbyType()).isEqualTo(LobbyType.LOAD_GAME.name());
        assertThat(result.getOwnUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void createLobby() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.createLobby(new OneParamRequest<>(LOBBY_NAME), accessTokenHeader);

        verify(lobbyCreationService).createNew(USER_ID, LOBBY_NAME);
    }

    @Test
    public void exitFromLobby() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.exitFromLobby(accessTokenHeader);

        verify(exitFromLobbyService).exit(USER_ID);
    }

    @Test
    public void inviteToLobby() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.inviteToLobby(FRIEND_ID, accessTokenHeader);

        verify(invitationService).invite(accessTokenHeader, FRIEND_ID);
    }

    @Test
    public void acceptInvitation() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.acceptInvitation(FRIEND_ID, accessTokenHeader);

        verify(joinToLobbyService).acceptInvitation(USER_ID, FRIEND_ID);
    }

    @Test
    public void getMembersOfLobby() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(lobbyMemberQueryService.getMembers(USER_ID)).willReturn(List.of(lobbyMemberResponse));

        List<LobbyMemberResponse> result = underTest.getMembersOfLobby(accessTokenHeader);

        assertThat(result).containsExactly(lobbyMemberResponse);
    }

    @Test
    public void startGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.startGame(accessTokenHeader);

        verify(startGameService).startGame(USER_ID);
    }

    @Test
    public void getActiveFriends() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(activeFriendsService.getActiveFriends(accessTokenHeader)).willReturn(Arrays.asList(activeFriendResponse));

        List<ActiveFriendResponse> result = underTest.getActiveFriends(accessTokenHeader);

        assertThat(result).containsExactly(activeFriendResponse);
    }

    @Test
    public void loadGame() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);

        underTest.loadGame(GAME_ID, accessTokenHeader);

        verify(lobbyCreationService).createForExistingGame(USER_ID, GAME_ID);
    }

    @Test
    void gameLoaded() {
        underTest.gameLoaded(GAME_ID);

        then(gameLoadedService).should().gameLoaded(GAME_ID);
    }
}