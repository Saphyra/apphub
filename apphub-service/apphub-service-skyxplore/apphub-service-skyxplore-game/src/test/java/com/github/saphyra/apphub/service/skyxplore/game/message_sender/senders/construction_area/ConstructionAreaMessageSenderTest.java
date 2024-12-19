package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.construction_area;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;
import com.github.saphyra.apphub.service.skyxplore.game.ws.planet.SkyXploreGameConstructionAreaWebSocketHandler;
import org.junit.jupiter.api.AfterEach;
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
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ConstructionAreaMessageSenderTest {
    private static final String SESSION_ID = "session-id";
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final WsSessionConstructionAreaIdMapping CONNECTED_USER = new WsSessionConstructionAreaIdMapping(SESSION_ID, CONSTRUCTION_AREA_ID, USER_ID);
    private static final String KEY = "key";
    private static final Object VALUE = "value";

    @Mock
    private SkyXploreGameConstructionAreaWebSocketHandler constructionAreaWebSocketHandler;

    @Mock
    private ConstructionAreaMessageProvider messageProvider;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private ErrorReporterService errorReporterService;

    private ConstructionAreaMessageSender underTest;

    @BeforeEach
    void setUp() {
        underTest = ConstructionAreaMessageSender.builder()
            .constructionAreaWebSocketHandler(constructionAreaWebSocketHandler)
            .messageProviders(List.of(messageProvider))
            .errorReporterService(errorReporterService)
            .executorServiceBean(executorServiceBean)
            .build();

        given(constructionAreaWebSocketHandler.getConnectedUsers()).willReturn(List.of(CONNECTED_USER));
        given(executorServiceBean.asyncProcess(any())).willAnswer(invocationOnMock -> CompletableFuture.completedFuture(ExecutionResult.success(invocationOnMock.getArgument(0, Callable.class).call())));
    }

    @AfterEach
    void verify() {
        then(messageProvider).should().clearDisconnectedUserData(List.of(CONNECTED_USER));
    }

    @Test
    void sendMessage() throws ExecutionException, InterruptedException {
        given(messageProvider.getMessage(SESSION_ID, USER_ID, CONSTRUCTION_AREA_ID)).willReturn(Optional.of(new UpdateItem(KEY, VALUE)));

        List<Future<ExecutionResult<Boolean>>> result = underTest.sendMessages();

        assertThat(result).hasSize(1);

        boolean resultValue = result.get(0)
            .get()
            .getOrThrow();
        assertThat(resultValue).isTrue();

        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(constructionAreaWebSocketHandler).should().sendEventToSession(eq(SESSION_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue())
            .returns(WebSocketEventName.SKYXPLORE_GAME_CONSTRUCTION_AREA_MODIFIED, WebSocketEvent::getEventName)
            .returns(Map.of(KEY, VALUE), WebSocketEvent::getPayload);
    }

    @Test
    void payloadEmpty() throws ExecutionException, InterruptedException {
        given(messageProvider.getMessage(SESSION_ID, USER_ID, CONSTRUCTION_AREA_ID)).willReturn(Optional.empty());

        List<Future<ExecutionResult<Boolean>>> result = underTest.sendMessages();

        assertThat(result).hasSize(1);

        boolean resultValue = result.get(0)
            .get()
            .getOrThrow();
        assertThat(resultValue).isFalse();

        then(constructionAreaWebSocketHandler).should(times(0)).sendEventToSession(any(String.class), any(WebSocketEvent.class));
    }

    @Test
    void exception() throws ExecutionException, InterruptedException {
        RuntimeException exception = new RuntimeException();
        given(messageProvider.getMessage(SESSION_ID, USER_ID, CONSTRUCTION_AREA_ID)).willThrow(exception);

        List<Future<ExecutionResult<Boolean>>> result = underTest.sendMessages();

        assertThat(result).hasSize(1);

        boolean resultValue = result.get(0)
            .get()
            .getOrThrow();
        assertThat(resultValue).isFalse();

        then(constructionAreaWebSocketHandler).should(times(0)).sendEventToSession(any(String.class), any(WebSocketEvent.class));
        then(errorReporterService).should().report(any(), eq(exception));
    }
}