package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.MessageDelay;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util.MessageSenderUtil;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PopulationQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import com.github.saphyra.apphub.service.skyxplore.game.ws.population.SkyXploreGamePopulationWebSocketHandler;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PopulationMessageSenderTest {
    private static final String SESSION_ID = "session-id";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final Long MESSAGE_DELAY = 24L;
    private static final String FIELD_LAST_MESSAGES = "lastMessages";
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private SkyXploreGamePopulationWebSocketHandler populationWebSocketHandler;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PopulationQueryService populationQueryService;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private MessageSenderUtil messageSenderUtil;

    private PopulationMessageSender underTest;

    @Mock
    private WsSessionPlanetIdMapping planetIdMapping;

    @Mock
    private MessageDelay messageDelay;

    @Mock
    private CitizenResponse citizenResponse;

    @BeforeEach
    void setUp() {
        underTest = PopulationMessageSender.builder()
            .populationWebSocketHandler(populationWebSocketHandler)
            .executorServiceBean(ExecutorServiceBeenTestUtils.create(errorReporterService))
            .dateTimeUtil(dateTimeUtil)
            .populationQueryService(populationQueryService)
            .gameProperties(gameProperties)
            .messageSenderUtil(messageSenderUtil)
            .errorReporterService(errorReporterService)
            .build();
    }

    @Test
    void sendMessages_lastMessageValid() throws ExecutionException, InterruptedException {
        given(populationWebSocketHandler.getConnectedUsers()).willReturn(List.of(planetIdMapping));

        given(planetIdMapping.getSessionId()).willReturn(SESSION_ID);
        given(planetIdMapping.getUserId()).willReturn(USER_ID);
        given(planetIdMapping.getPlanetId()).willReturn(PLANET_ID);

        given(gameProperties.getMessageDelay()).willReturn(messageDelay);
        given(messageDelay.getPopulation()).willReturn(MESSAGE_DELAY);
        given(messageSenderUtil.lastMessageValid(SESSION_ID, Collections.emptyMap(), MESSAGE_DELAY)).willReturn(true);

        List<FutureWrapper<Boolean>> result = underTest.sendMessages();

        assertThat(result.get(0).get().getOrThrow()).isFalse();

        then(messageSenderUtil).should().clearDisconnectedUserData(List.of(planetIdMapping), Collections.emptyMap());
        then(populationWebSocketHandler).should(times(0)).sendEventToSession(any(), any());
    }

    @Test
    void sendMessages_payloadIsTheSame() throws ExecutionException, InterruptedException, IllegalAccessException {
        Map<String, LastMessage<List<CitizenResponse>>> lastMessages = CollectionUtils.singleValueMap(SESSION_ID, new LastMessage<>(List.of(citizenResponse), null));
        FieldUtils.writeField(underTest, FIELD_LAST_MESSAGES, lastMessages, true);
        given(populationWebSocketHandler.getConnectedUsers()).willReturn(List.of(planetIdMapping));

        given(planetIdMapping.getSessionId()).willReturn(SESSION_ID);
        given(planetIdMapping.getUserId()).willReturn(USER_ID);
        given(planetIdMapping.getPlanetId()).willReturn(PLANET_ID);

        given(gameProperties.getMessageDelay()).willReturn(messageDelay);
        given(messageDelay.getPopulation()).willReturn(MESSAGE_DELAY);
        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, MESSAGE_DELAY)).willReturn(false);
        given(populationQueryService.getPopulation(USER_ID, PLANET_ID)).willReturn(List.of(citizenResponse));

        List<FutureWrapper<Boolean>> result = underTest.sendMessages();

        assertThat(result.get(0).get().getOrThrow()).isFalse();

        then(messageSenderUtil).should().clearDisconnectedUserData(List.of(planetIdMapping), lastMessages);
        then(populationWebSocketHandler).should(times(0)).sendEventToSession(any(), any());
    }

    @Test
    void sendMessages() throws ExecutionException, InterruptedException, IllegalAccessException {
        Map<String, LastMessage<List<CitizenResponse>>> lastMessages = CollectionUtils.singleValueMap(SESSION_ID, new LastMessage<>(Collections.emptyList(), null));
        FieldUtils.writeField(underTest, FIELD_LAST_MESSAGES, lastMessages, true);
        given(populationWebSocketHandler.getConnectedUsers()).willReturn(List.of(planetIdMapping));

        given(planetIdMapping.getSessionId()).willReturn(SESSION_ID);
        given(planetIdMapping.getUserId()).willReturn(USER_ID);
        given(planetIdMapping.getPlanetId()).willReturn(PLANET_ID);

        given(gameProperties.getMessageDelay()).willReturn(messageDelay);
        given(messageDelay.getPopulation()).willReturn(MESSAGE_DELAY);
        given(messageSenderUtil.lastMessageValid(SESSION_ID, lastMessages, MESSAGE_DELAY)).willReturn(false);
        given(populationQueryService.getPopulation(USER_ID, PLANET_ID)).willReturn(List.of(citizenResponse));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);

        List<FutureWrapper<Boolean>> result = underTest.sendMessages();

        assertThat(result.get(0).get().getOrThrow()).isTrue();

        then(messageSenderUtil).should().clearDisconnectedUserData(List.of(planetIdMapping), lastMessages);

        ArgumentCaptor<WebSocketEvent> argumentCaptor = ArgumentCaptor.forClass(WebSocketEvent.class);
        then(populationWebSocketHandler).should().sendEventToSession(eq(SESSION_ID), argumentCaptor.capture());
        WebSocketEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_GAME_POPULATION_MODIFIED);
        assertThat(event.getPayload()).isEqualTo(List.of(citizenResponse));

        assertThat(lastMessages).containsEntry(SESSION_ID, new LastMessage<>(List.of(citizenResponse), CURRENT_TIME));
    }
}