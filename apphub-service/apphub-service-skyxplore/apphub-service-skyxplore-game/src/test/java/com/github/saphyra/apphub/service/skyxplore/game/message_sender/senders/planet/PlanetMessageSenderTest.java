package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import com.github.saphyra.apphub.service.skyxplore.game.ws.planet.SkyXploreGamePlanetWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PlanetMessageSenderTest {
    private static final String SESSION_ID = "session-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String UPDATE_ITEM_KEY = "update-item-key";
    private static final Object UPDATE_ITEM_VALUE = "update-item-value";

    @Mock
    private PlanetMessageProvider messageProvider;

    @Mock
    private SkyXploreGamePlanetWebSocketHandler planetWebSocketHandler;

    @Mock
    private ErrorReporterService errorReporterService;

    private PlanetMessageSender underTest;

    @Mock
    private WsSessionPlanetIdMapping planetIdMapping;

    @BeforeEach
    void setUp() {
        underTest = PlanetMessageSender.builder()
            .messageProviders(List.of(messageProvider))
            .errorReporterService(errorReporterService)
            .executorServiceBean(ExecutorServiceBeenTestUtils.create(errorReporterService))
            .planetWebSocketHandler(planetWebSocketHandler)
            .build();
    }

    @Test
    void sendMessages() throws Exception {
        given(planetWebSocketHandler.getConnectedUsers()).willReturn(List.of(planetIdMapping));

        given(planetIdMapping.getSessionId()).willReturn(SESSION_ID);
        given(planetIdMapping.getUserId()).willReturn(USER_ID);
        given(planetIdMapping.getPlanetId()).willReturn(PLANET_ID);

        given(messageProvider.getMessage(SESSION_ID, USER_ID, PLANET_ID)).willReturn(Optional.of(new UpdateItem(UPDATE_ITEM_KEY, UPDATE_ITEM_VALUE)));

        List<Future<ExecutionResult<Boolean>>> result = underTest.sendMessages();

        assertThat(result.get(0).get().getOrThrow()).isTrue();

        then(messageProvider).should().clearDisconnectedUserData(List.of(planetIdMapping));

        ArgumentCaptor<WebSocketEvent> wsEventArgumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(planetWebSocketHandler).should().sendEventToSession(eq(SESSION_ID), wsEventArgumentCaptor.capture());
        WebSocketEvent event = wsEventArgumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED);
        assertThat((Map<String, Object>) event.getPayload()).containsEntry(UPDATE_ITEM_KEY, UPDATE_ITEM_VALUE);

        assertThat(result).extracting(executionResultFuture -> executionResultFuture.get().getOrThrow()).containsExactly(true);
    }
}