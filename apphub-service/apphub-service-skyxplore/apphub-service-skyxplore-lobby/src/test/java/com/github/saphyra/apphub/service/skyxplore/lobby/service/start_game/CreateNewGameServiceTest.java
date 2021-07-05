package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.GameSettings;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CreateNewGameServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final String LOCALE = "locale";
    private static final String GAME_NAME = "game-name";

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private SkyXploreGameCreationApiClient gameCreationClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private CreateNewGameService underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Member member;

    @Mock
    private Alliance alliance;

    @Test
    public void unreadyMember() {
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, member));
        given(member.getStatus()).willReturn(LobbyMemberStatus.NOT_READY);

        Throwable ex = catchThrowable(() -> underTest.createNewGame(lobby));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.PRECONDITION_FAILED, ErrorCode.LOBBY_MEMBER_NOT_READY);
    }

    @Test
    public void startGame() {
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, member));
        given(lobby.getHost()).willReturn(USER_ID);
        given(member.getStatus()).willReturn(LobbyMemberStatus.READY);
        given(member.getAlliance()).willReturn(ALLIANCE_ID);
        given(lobby.getAlliances()).willReturn(Arrays.asList(alliance));
        given(alliance.getAllianceName()).willReturn(ALLIANCE_NAME);
        given(alliance.getAllianceId()).willReturn(ALLIANCE_ID);
        GameSettings gameSettings = new GameSettings();
        given(lobby.getSettings()).willReturn(gameSettings);
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
        given(lobby.getLobbyName()).willReturn(GAME_NAME);
        given(member.getUserId()).willReturn(USER_ID);

        underTest.createNewGame(lobby);

        ArgumentCaptor<SkyXploreGameCreationRequest> gameCreationRequestArgumentCaptor = ArgumentCaptor.forClass(SkyXploreGameCreationRequest.class);
        verify(gameCreationClient).createGame(gameCreationRequestArgumentCaptor.capture(), eq(LOCALE));
        SkyXploreGameCreationRequest gameCreationRequest = gameCreationRequestArgumentCaptor.getValue();
        assertThat(gameCreationRequest.getHost()).isEqualTo(USER_ID);
        assertThat(gameCreationRequest.getMembers()).containsEntry(USER_ID, ALLIANCE_ID);
        assertThat(gameCreationRequest.getAlliances()).containsEntry(ALLIANCE_ID, ALLIANCE_NAME);
        assertThat(gameCreationRequest.getGameName()).isEqualTo(GAME_NAME);

        SkyXploreGameCreationSettingsRequest settings = gameCreationRequest.getSettings();
        assertThat(settings.getUniverseSize()).isEqualTo(gameSettings.getUniverseSize());
        assertThat(settings.getSystemAmount()).isEqualTo(gameSettings.getSystemAmount());
        assertThat(settings.getSystemSize()).isEqualTo(gameSettings.getSystemSize());
        assertThat(settings.getPlanetSize()).isEqualTo(gameSettings.getPlanetSize());
        assertThat(settings.getAiPresence()).isEqualTo(gameSettings.getAiPresence());

        verify(lobby).setGameCreationStarted(true);

        ArgumentCaptor<WebSocketMessage> webSocketMessageArgumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(webSocketMessageArgumentCaptor.capture());
        WebSocketMessage message = webSocketMessageArgumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(USER_ID);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED);

        verify(lobbyDao).delete(lobby);
    }
}