package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.AllianceCreatedResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler.ChatSendMessageWebSocketEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageSenderProxyTest {
    private static final String LOCALE = "locale";
    private static final UUID RECIPIENT = UUID.randomUUID();

    @Mock
    private MessageSenderApiClient messageSenderApiClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private MessageSenderProxy underTest;

    @Mock
    private WebSocketMessage message;

    @Mock
    private LobbyMemberResponse lobbyMemberResponse;

    @Mock
    private AllianceCreatedResponse allianceCreatedResponse;

    @Mock
    private AiPlayer aiPlayer;

    @Mock
    private ChatSendMessageWebSocketEventHandler.Message chatMessage;

    @Captor
    private ArgumentCaptor<WebSocketMessage> argumentCaptor;

    @BeforeEach
    public void setUp() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
    }

    @Test
    public void sendToMainMenu() {
        underTest.sendToMainMenu(message);

        verify(messageSenderApiClient).sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, LOCALE);
    }

    @Test
    public void sendToLobby() {
        underTest.sendToLobby(message);

        verify(messageSenderApiClient).sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, LOCALE);
    }

    @Test
    void lobbyMemberModified() {
        underTest.lobbyMemberModified(lobbyMemberResponse, List.of(RECIPIENT));

        verify(messageSenderApiClient).sendMessage(eq(MessageGroup.SKYXPLORE_LOBBY), argumentCaptor.capture(), eq(LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED);
        assertThat(message.getEvent().getPayload()).isEqualTo(lobbyMemberResponse);
        assertThat(message.getRecipients()).containsExactly(RECIPIENT);
    }

    @Test
    void allianceCreated() {
        underTest.allianceCreated(allianceCreatedResponse, List.of(RECIPIENT));

        verify(messageSenderApiClient).sendMessage(eq(MessageGroup.SKYXPLORE_LOBBY), argumentCaptor.capture(), eq(LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_ALLIANCE_CREATED);
        assertThat(message.getEvent().getPayload()).isEqualTo(allianceCreatedResponse);
        assertThat(message.getRecipients()).containsExactly(RECIPIENT);
    }

    @Test
    void aiModified() {
        underTest.aiModified(aiPlayer, List.of(RECIPIENT));

        verify(messageSenderApiClient).sendMessage(eq(MessageGroup.SKYXPLORE_LOBBY), argumentCaptor.capture(), eq(LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED);
        assertThat(message.getEvent().getPayload()).isEqualTo(aiPlayer);
        assertThat(message.getRecipients()).containsExactly(RECIPIENT);
    }

    @Test
    void sendLobbyChatMessage() {
        underTest.sendLobbyChatMessage(chatMessage, List.of(RECIPIENT));

        verify(messageSenderApiClient).sendMessage(eq(MessageGroup.SKYXPLORE_LOBBY), argumentCaptor.capture(), eq(LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE);
        assertThat(message.getEvent().getPayload()).isEqualTo(chatMessage);
        assertThat(message.getRecipients()).containsExactly(RECIPIENT);
    }

    @Test
    void lobbyMemberConnected() {
        underTest.lobbyMemberConnected(lobbyMemberResponse, List.of(RECIPIENT));

        verify(messageSenderApiClient).sendMessage(eq(MessageGroup.SKYXPLORE_LOBBY), argumentCaptor.capture(), eq(LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_CONNECTED);
        assertThat(message.getEvent().getPayload()).isEqualTo(lobbyMemberResponse);
        assertThat(message.getRecipients()).containsExactly(RECIPIENT);
    }

    @Test
    void lobbyMemberDisconnected() {
        underTest.lobbyMemberDisconnected(lobbyMemberResponse, List.of(RECIPIENT));

        verify(messageSenderApiClient).sendMessage(eq(MessageGroup.SKYXPLORE_LOBBY), argumentCaptor.capture(), eq(LOCALE));

        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED);
        assertThat(message.getEvent().getPayload()).isEqualTo(lobbyMemberResponse);
        assertThat(message.getRecipients()).containsExactly(RECIPIENT);
    }
}