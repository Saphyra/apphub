package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings.alliance;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceCreatedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.player.LobbyPlayerToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AllianceServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String NO_ALLIANCE = "no-alliance";
    private static final String NEW_ALLIANCE = "new-alliance";
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_ID_STRING = "alliance-id";

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private LobbyPlayerToResponseConverter lobbyPlayerToResponseConverter;

    @Mock
    private AllianceToResponseConverter allianceToResponseConverter;

    @Mock
    private AllianceFactory allianceFactory;

    @InjectMocks
    private AllianceService underTest;

    @Mock
    private Alliance alliance;

    @Mock
    private AllianceResponse allianceResponse;

    @Mock
    private Lobby lobby;

    @Mock
    private AiPlayer aiPlayer;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Mock
    private LobbyPlayerResponse lobbyPlayerResponse;

    @Test
    void getAlliances() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getAlliances()).willReturn(List.of(alliance));
        given(allianceToResponseConverter.convertToResponse(alliance)).willReturn(allianceResponse);

        assertThat(underTest.getAlliances(USER_ID)).containsExactly(allianceResponse);
    }

    @Test
    void setAllianceOfAi_notHost() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.setAllianceOfAi(USER_ID, PLAYER_ID, null));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    void setAllianceOfAi_aiNotFound() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getAis()).willReturn(Collections.emptyList());

        Throwable ex = catchThrowable(() -> underTest.setAllianceOfAi(USER_ID, PLAYER_ID, null));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void setAllianceOfAi_noAlliance() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getAis()).willReturn(List.of(aiPlayer));
        given(aiPlayer.getUserId()).willReturn(PLAYER_ID);
        Map<UUID, LobbyPlayer> players = Map.of(PLAYER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);

        underTest.setAllianceOfAi(USER_ID, PLAYER_ID, NO_ALLIANCE);

        verify(aiPlayer).setAllianceId(null);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED, aiPlayer);
    }

    @Test
    void setAllianceOfAi_newAlliance() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getAis()).willReturn(List.of(aiPlayer));
        given(aiPlayer.getUserId()).willReturn(PLAYER_ID);
        Map<UUID, LobbyPlayer> players = Map.of(PLAYER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        List<Alliance> alliances = new ArrayList<>();
        given(lobby.getAlliances()).willReturn(alliances);
        given(allianceFactory.create(0)).willReturn(alliance);
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(allianceToResponseConverter.convertToResponse(alliance)).willReturn(allianceResponse);

        underTest.setAllianceOfAi(USER_ID, PLAYER_ID, NEW_ALLIANCE);

        assertThat(alliances).containsExactly(alliance);
        verify(aiPlayer).setAllianceId(ALLIANCE_ID);

        ArgumentCaptor<AllianceCreatedResponse> argumentCaptor = ArgumentCaptor.forClass(AllianceCreatedResponse.class);
        verify(lobbyWebSocketHandler).sendEvent(eq(players.keySet()), eq(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAlliance()).isEqualTo(allianceResponse);
        assertThat(argumentCaptor.getValue().getAi()).isEqualTo(aiPlayer);
        assertThat(argumentCaptor.getValue().getPlayer()).isNull();
    }

    @Test
    void setAllianceOfAi_existingAlliance() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobby.getAis()).willReturn(List.of(aiPlayer));
        given(aiPlayer.getUserId()).willReturn(PLAYER_ID);
        Map<UUID, LobbyPlayer> players = Map.of(PLAYER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        given(uuidConverter.convertEntity(ALLIANCE_ID_STRING)).willReturn(ALLIANCE_ID);

        underTest.setAllianceOfAi(USER_ID, PLAYER_ID, ALLIANCE_ID_STRING);

        verify(aiPlayer).setAllianceId(ALLIANCE_ID);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED, aiPlayer);
    }

    @Test
    void setAllianceOfPlayer_notHost() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.setAllianceOfPlayer(USER_ID, PLAYER_ID, null));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION);
    }

    @Test
    void setAllianceOfPlayer_noAlliance() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        Map<UUID, LobbyPlayer> players = Map.of(PLAYER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        given(lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer)).willReturn(lobbyPlayerResponse);

        underTest.setAllianceOfPlayer(USER_ID, PLAYER_ID, NO_ALLIANCE);

        verify(lobbyPlayer).setAllianceId(null);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyPlayerResponse);
    }

    @Test
    void setAllianceOfPlayer_newAlliance() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        Map<UUID, LobbyPlayer> players = Map.of(PLAYER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        List<Alliance> alliances = new ArrayList<>();
        given(lobby.getAlliances()).willReturn(alliances);
        given(allianceFactory.create(0)).willReturn(alliance);
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(allianceToResponseConverter.convertToResponse(alliance)).willReturn(allianceResponse);
        given(lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer)).willReturn(lobbyPlayerResponse);

        underTest.setAllianceOfPlayer(USER_ID, PLAYER_ID, NEW_ALLIANCE);

        assertThat(alliances).containsExactly(alliance);
        verify(lobbyPlayer).setAllianceId(ALLIANCE_ID);

        ArgumentCaptor<AllianceCreatedResponse> argumentCaptor = ArgumentCaptor.forClass(AllianceCreatedResponse.class);
        verify(lobbyWebSocketHandler).sendEvent(eq(players.keySet()), eq(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getAlliance()).isEqualTo(allianceResponse);
        assertThat(argumentCaptor.getValue().getAi()).isNull();
        assertThat(argumentCaptor.getValue().getPlayer()).isEqualTo(lobbyPlayerResponse);
    }

    @Test
    void setAllianceOfPlayer_existingAlliance() {
        given(lobbyDao.findByUserIdValidated(USER_ID)).willReturn(lobby);
        given(lobby.getHost()).willReturn(USER_ID);
        Map<UUID, LobbyPlayer> players = Map.of(PLAYER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        given(uuidConverter.convertEntity(ALLIANCE_ID_STRING)).willReturn(ALLIANCE_ID);
        given(lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer)).willReturn(lobbyPlayerResponse);

        underTest.setAllianceOfPlayer(USER_ID, PLAYER_ID, ALLIANCE_ID_STRING);

        verify(lobbyPlayer).setAllianceId(ALLIANCE_ID);
        then(lobbyWebSocketHandler).should().sendEvent(players.keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyPlayerResponse);
    }
}