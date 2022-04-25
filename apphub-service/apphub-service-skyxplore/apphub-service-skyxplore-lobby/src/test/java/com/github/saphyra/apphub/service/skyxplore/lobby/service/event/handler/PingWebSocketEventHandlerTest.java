package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PingWebSocketEventHandlerTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private LobbyDao lobbyDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private PingWebSocketEventHandler underTest;

    @Mock
    private Lobby lobby;

    @Test
    public void canHandle_pingEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.PING)).isTrue();
    }

    @Test
    public void canHandle_otherEvent() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }

    @Test
    public void handle() {
        given(lobbyDao.findByUserId(FROM)).willReturn(Optional.of(lobby));
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);

        underTest.handle(FROM, null);

        verify(lobby).setLastAccess(CURRENT_DATE);
    }
}