package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenResponse;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.MessageSender;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PopulationQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import com.github.saphyra.apphub.service.skyxplore.game.ws.population.SkyXploreGamePopulationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO split
//TODO unit test
public class PopulationMessageSender implements MessageSender {
    private final SkyXploreGamePopulationWebSocketHandler populationWebSocketHandler;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;
    private final DateTimeUtil dateTimeUtil;
    private final PopulationQueryService populationQueryService;
    private final GameProperties gameProperties;

    private final Map<String, LastMessage<List<CitizenResponse>>> lastMessages = new ConcurrentHashMap<>();

    @Override
    public List<Future<ExecutionResult<Boolean>>> sendMessages() {
        List<WsSessionPlanetIdMapping> connectedUsers = populationWebSocketHandler.getConnectedUsers();

        clearDisconnectedUserData(connectedUsers);

        return connectedUsers.stream()
            .map(mapping -> sendMessage(mapping.getSessionId(), mapping.getUserId(), mapping.getPlanetId()))
            .toList();
    }

    private void clearDisconnectedUserData(List<WsSessionPlanetIdMapping> connectedUsers) {
        List<String> connectedSessions = connectedUsers.stream()
            .map(WsSessionPlanetIdMapping::getSessionId)
            .toList();

        List<String> entriesToRemove = lastMessages.keySet()
            .stream()
            .filter(sessionId -> !connectedSessions.contains(sessionId))
            .toList();

        entriesToRemove.forEach(lastMessages::remove);
    }

    private Future<ExecutionResult<Boolean>> sendMessage(String sessionId, UUID userId, UUID planetId) {
        return executorServiceBean.asyncProcess(() -> {
            try {
                List<CitizenResponse> payload = getPayload(sessionId, userId, planetId);

                if (payload.isEmpty()) {
                    log.debug("No messages to send to user {} for population on planet {}", userId, planetId);
                    return false;
                }

                WebSocketEvent event = WebSocketEvent.builder()
                    .eventName(WebSocketEventName.SKYXPLORE_GAME_POPULATION_MODIFIED)
                    .payload(payload)
                    .build();
                populationWebSocketHandler.sendEventToSession(sessionId, event);

                return true;
            } catch (Exception e) {
                String errorMessage = String.format("Failed sending Population update for user %s on planet %s", userId, planetId);
                log.error(errorMessage, e);
                errorReporterService.report(errorMessage, e);
                return false;
            }
        });
    }

    private List<CitizenResponse> getPayload(String sessionId, UUID userId, UUID planetId) {
        if (!shouldSend(sessionId)) {
            log.debug("Last Population status is still valid for user {} on planet {}", userId, planetId);
            return Collections.emptyList();
        }

        List<CitizenResponse> actualPayload = populationQueryService.getPopulation(userId, planetId);
        List<CitizenResponse> lastMessage = Optional.ofNullable(lastMessages.get(sessionId))
            .map(LastMessage::getPayload)
            .orElse(Collections.emptyList());

        if (actualPayload.size() == lastMessage.size() && new HashSet<>(actualPayload).containsAll(lastMessage)) {
            log.debug("No Population update necessary for userId {} on planet {}", userId, planetId);
            return Collections.emptyList();
        }

        LastMessage<List<CitizenResponse>> latest = LastMessage.<List<CitizenResponse>>builder()
            .payload(actualPayload)
            .sentAt(dateTimeUtil.getCurrentDateTime())
            .build();
        lastMessages.put(sessionId, latest);

        return actualPayload;
    }

    private boolean shouldSend(String sessionId) {
        LastMessage<?> lastMessage = lastMessages.get(sessionId);
        if (isNull(lastMessage)) {
            return true;
        }

        return lastMessage.getSentAt().isBefore(dateTimeUtil.getCurrentDateTime().minusNanos(gameProperties.getMessageDelay().getPopulation() * 1000));
    }
}
