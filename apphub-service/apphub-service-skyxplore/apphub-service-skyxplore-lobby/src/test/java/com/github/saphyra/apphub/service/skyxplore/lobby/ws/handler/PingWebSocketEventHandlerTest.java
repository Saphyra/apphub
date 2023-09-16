package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        underTest.handle(FROM, null, null);

        verify(lobby).setLastAccess(CURRENT_DATE);
    }
}