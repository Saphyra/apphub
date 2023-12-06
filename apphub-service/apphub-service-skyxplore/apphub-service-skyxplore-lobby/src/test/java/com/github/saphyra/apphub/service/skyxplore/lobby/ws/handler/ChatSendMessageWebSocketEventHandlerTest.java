package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class ChatSendMessageWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "player-name";
    private static final Object EVENT_PAYLOAD = "event-payload";
    private static final long CREATED_AT = 3432L;

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private CharacterProxy characterProxy;

    @Mock
    private SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private ChatSendMessageWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private LobbyPlayer lobbyPlayer;

    @Mock
    private SkyXploreCharacterModel characterModel;

    @Mock
    private WebSocketEvent receivedEvent;

    @Test
    public void canHandle_sendMessageEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void handle() {
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        Map<UUID, LobbyPlayer> players = CollectionUtils.singleValueMap(MEMBER_ID, lobbyPlayer);
        given(lobby.getPlayers()).willReturn(players);
        given(characterProxy.getCharacter(FROM)).willReturn(characterModel);
        given(characterModel.getName()).willReturn(PLAYER_NAME);
        given(receivedEvent.getPayload()).willReturn(EVENT_PAYLOAD);
        given(dateTimeUtil.getCurrentTimeEpochMillis()).willReturn(CREATED_AT);

        underTest.handle(FROM, receivedEvent, lobbyWebSocketHandler);

        ArgumentCaptor<ChatSendMessageWebSocketEventHandler.Message> argumentCaptor = ArgumentCaptor.forClass(ChatSendMessageWebSocketEventHandler.Message.class);
        then(lobbyWebSocketHandler).should().sendEvent(eq(players.keySet()), eq(WebSocketEventName.SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE), argumentCaptor.capture());

        ChatSendMessageWebSocketEventHandler.Message payload = argumentCaptor.getValue();
        assertThat(payload.getSenderId()).isEqualTo(FROM);
        assertThat(payload.getSenderName()).isEqualTo(PLAYER_NAME);
        assertThat(payload.getMessage()).isEqualTo(EVENT_PAYLOAD);
        assertThat(payload.getCreatedAt()).isEqualTo(CREATED_AT);
    }
}