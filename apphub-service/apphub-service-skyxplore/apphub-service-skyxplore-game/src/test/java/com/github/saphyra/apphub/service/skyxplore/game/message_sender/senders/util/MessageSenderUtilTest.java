package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageSenderUtilTest {
    private static final String DISCONNECTED_SESSION_ID = "disconnected-session-id";
    private static final String CONNECTED_SESSION_ID = "connected-session-id";
    private static final long POLLING_INTERVAL = 32;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private MessageSenderUtil underTest;

    @Mock
    private WsSessionPlanetIdMapping planetIdMapping;

    @Test
    void clearDisconnectedUserData() {
        Map<String, LastMessage<Object>> lastMessages = CollectionUtils.toMap(
            new BiWrapper<>(DISCONNECTED_SESSION_ID, new LastMessage<>(null, null)),
            new BiWrapper<>(CONNECTED_SESSION_ID, new LastMessage<>(null, null))
        );
        given(planetIdMapping.getSessionId()).willReturn(CONNECTED_SESSION_ID);

        underTest.clearDisconnectedUserData(List.of(planetIdMapping), lastMessages);

        assertThat(lastMessages).containsOnlyKeys(CONNECTED_SESSION_ID);
    }

    @Test
    void lastMessageValid_noLastMessage() {
        assertThat(underTest.lastMessageValid(CONNECTED_SESSION_ID, Collections.emptyMap(), POLLING_INTERVAL)).isFalse();
    }

    @Test
    void lastMessageValid_stillValid() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        Map<String, LastMessage<Object>> lastMessages = Map.of(CONNECTED_SESSION_ID, new LastMessage<>(null, CURRENT_TIME.minusNanos((POLLING_INTERVAL - 1) * 1000 * 1000)));

        assertThat(underTest.lastMessageValid(CONNECTED_SESSION_ID, lastMessages, POLLING_INTERVAL)).isTrue();
    }

    @Test
    void lastMessageValid_expired() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        Map<String, LastMessage<Object>> lastMessages = Map.of(CONNECTED_SESSION_ID, new LastMessage<>(null, CURRENT_TIME.minusNanos((POLLING_INTERVAL + 1) * 1000 * 1000)));

        assertThat(underTest.lastMessageValid(CONNECTED_SESSION_ID, lastMessages, POLLING_INTERVAL)).isFalse();
    }
}