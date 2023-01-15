package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.skyxplore.response.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.GameSettingsResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyViewForPage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.GameSettings;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.ExitFromLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.JoinToLobbyService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game.StartGameService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.LobbyCreationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.invite.InvitationService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreLobbyControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";
    private static final UUID HOST = UUID.randomUUID();
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
    private LobbyDao lobbyDao;

    @Mock
    private StartGameService startGameService;

    @InjectMocks
    private SkyXploreLobbyControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyMembersResponse lobbyMembersResponse;

    @Mock
    private ActiveFriendResponse activeFriendResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void createLobby() {
        underTest.createLobby(new OneParamRequest<>(LOBBY_NAME), accessTokenHeader);

        verify(lobbyCreationService).createNew(USER_ID, LOBBY_NAME);
    }

    @Test
    public void lobbyForPage_inLobby() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.of(lobby));
        given(lobby.getHost()).willReturn(HOST);
        given(lobby.isGameCreationStarted()).willReturn(true);
        given(lobby.getLobbyName()).willReturn(LOBBY_NAME);
        given(lobby.getType()).willReturn(LobbyType.NEW_GAME);

        LobbyViewForPage result = underTest.lobbyForPage(accessTokenHeader);

        assertThat(result.isInLobby()).isTrue();
        assertThat(result.isGameCreationStarted()).isTrue();
        assertThat(result.getHost()).isEqualTo(HOST);
        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.getType()).isEqualTo(LobbyType.NEW_GAME.name());
    }

    @Test
    public void lobbyForPage_notInLobby() {
        given(lobbyDao.findByUserId(USER_ID)).willReturn(Optional.empty());

        LobbyViewForPage result = underTest.lobbyForPage(accessTokenHeader);

        assertThat(result.isInLobby()).isFalse();
    }

    @Test
    public void exitFromLobby() {
        underTest.exitFromLobby(accessTokenHeader);

        verify(exitFromLobbyService).exit(USER_ID);
    }

    @Test
    public void inviteToLobby() {
        underTest.inviteToLobby(FRIEND_ID, accessTokenHeader);

        verify(invitationService).invite(accessTokenHeader, FRIEND_ID);
    }

    @Test
    public void acceptInvitation() {
        underTest.acceptInvitation(FRIEND_ID, accessTokenHeader);

        verify(joinToLobbyService).acceptInvitation(USER_ID, FRIEND_ID);
    }

    @Test
    public void userJoinedToLobby() {
        underTest.userJoinedToLobby(USER_ID);

        verify(joinToLobbyService).userJoinedToLobby(USER_ID);
    }

    @Test
    public void userLeftLobby() {
        underTest.userLeftLobby(USER_ID);

        verify(exitFromLobbyService).exit(USER_ID);
    }

    @Test
    public void getMembersOfLobby() {
        given(lobbyMemberQueryService.getMembers(USER_ID)).willReturn(lobbyMembersResponse);

        LobbyMembersResponse result = underTest.getMembersOfLobby(accessTokenHeader);

        assertThat(result).isEqualTo(lobbyMembersResponse);
    }

    @Test
    public void getGameSettings() {
        GameSettings gameSettings = new GameSettings();
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getSettings()).willReturn(gameSettings);

        GameSettingsResponse result = underTest.getGameSettings(accessTokenHeader);

        assertThat(result.getUniverseSize()).isEqualTo(gameSettings.getUniverseSize().name());
        assertThat(result.getSystemAmount()).isEqualTo(gameSettings.getSystemAmount().name());
        assertThat(result.getSystemSize()).isEqualTo(gameSettings.getSystemSize().name());
        assertThat(result.getPlanetSize()).isEqualTo(gameSettings.getPlanetSize().name());
        assertThat(result.getAiPresence()).isEqualTo(gameSettings.getAiPresence().name());
    }

    @Test
    public void startGame() {
        underTest.startGame(accessTokenHeader);

        verify(startGameService).startGame(USER_ID);
    }

    @Test
    public void getActiveFriends() {
        given(activeFriendsService.getActiveFriends(accessTokenHeader)).willReturn(Arrays.asList(activeFriendResponse));

        List<ActiveFriendResponse> result = underTest.getActiveFriends(accessTokenHeader);

        assertThat(result).containsExactly(activeFriendResponse);
    }

    @Test
    public void loadGame() {
        underTest.loadGame(GAME_ID, accessTokenHeader);

        verify(lobbyCreationService).createForExistingGame(USER_ID, GAME_ID);
    }
}