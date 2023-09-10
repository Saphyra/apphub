package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CreateNewGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final String LOCALE = "locale";
    private static final String GAME_NAME = "game-name";
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private SkyXploreGameCreationApiClient gameCreationClient;

    @Mock
    private LocaleProvider localeProvider;

    @Mock
    private AllianceSetupValidator allianceSetupValidator;

    @InjectMocks
    private CreateNewGameService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyMember lobbyMember;

    @Mock
    private Alliance alliance;

    @Mock
    private SkyXploreGameSettings settings;

    @Test
    public void startGame() {
        Map<UUID, LobbyMember> lobbyMembers = CollectionUtils.singleValueMap(USER_ID, lobbyMember);
        given(lobby.getMembers()).willReturn(lobbyMembers);
        given(lobby.getHost()).willReturn(USER_ID);
        given(lobbyMember.getAllianceId()).willReturn(ALLIANCE_ID);
        given(lobby.getAlliances()).willReturn(Arrays.asList(alliance));
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        given(lobby.getSettings()).willReturn(settings);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(lobby.getLobbyName()).willReturn(GAME_NAME);
        given(lobbyMember.getUserId()).willReturn(USER_ID);
        given(gameCreationClient.createGame(any(SkyXploreGameCreationRequest.class), eq(LOCALE))).willReturn(GAME_ID);

        underTest.createNewGame(lobby);

        ArgumentCaptor<SkyXploreGameCreationRequest> gameCreationRequestArgumentCaptor = ArgumentCaptor.forClass(SkyXploreGameCreationRequest.class);
        verify(gameCreationClient).createGame(gameCreationRequestArgumentCaptor.capture(), eq(LOCALE));
        SkyXploreGameCreationRequest gameCreationRequest = gameCreationRequestArgumentCaptor.getValue();
        assertThat(gameCreationRequest.getHost()).isEqualTo(USER_ID);
        assertThat(gameCreationRequest.getMembers()).containsEntry(USER_ID, ALLIANCE_ID);
        assertThat(gameCreationRequest.getAlliances()).containsEntry(ALLIANCE_ID, ALLIANCE_NAME);
        assertThat(gameCreationRequest.getGameName()).isEqualTo(GAME_NAME);
        assertThat(gameCreationRequest.getSettings()).isEqualTo(settings);

        verify(allianceSetupValidator).check(gameCreationRequest);

        verify(lobby).setGameCreationStarted(true);
        then(lobby).should().setGameId(GAME_ID);

        then(lobbyWebSocketHandler).should().sendEvent(lobbyMembers.keySet(), WebSocketEvent.builder().eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED).build());
    }
}