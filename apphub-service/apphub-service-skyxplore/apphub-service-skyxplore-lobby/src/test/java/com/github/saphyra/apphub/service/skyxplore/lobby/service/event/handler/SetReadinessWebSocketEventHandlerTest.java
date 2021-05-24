package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SetReadinessWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();
    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @InjectMocks
    private SetReadinessWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Mock
    private Member member;

    @Test
    public void canHandle_setReadinessEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void setReadiness() {
        given(lobbyDao.findByUserIdValidated(FROM)).willReturn(lobby);
        given(lobby.getMembers()).willReturn(CollectionUtils.singleValueMap(FROM, member));

        underTest.handle(FROM, WebSocketEvent.builder().payload(String.valueOf(true)).build());

        verify(member).setReady(true);

        ArgumentCaptor<WebSocketMessage> argumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(argumentCaptor.capture());
        WebSocketMessage message = argumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(FROM);

        WebSocketEvent event = message.getEvent();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS);

        SetReadinessWebSocketEventHandler.ReadinessEvent payload = (SetReadinessWebSocketEventHandler.ReadinessEvent) event.getPayload();

        assertThat(payload.getUserId()).isEqualTo(FROM);
        assertThat(payload.isReady()).isTrue();
    }
}