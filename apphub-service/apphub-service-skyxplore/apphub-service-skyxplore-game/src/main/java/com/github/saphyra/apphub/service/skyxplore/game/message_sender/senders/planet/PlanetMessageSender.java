package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.MessageSender;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.planet.SkyXploreGamePlanetWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Builder
@Component
@RequiredArgsConstructor
@Slf4j
class PlanetMessageSender implements MessageSender {
    private final List<PlanetMessageProvider> messageProviders;
    private final ExecutorServiceBean executorServiceBean;
    private final SkyXploreGamePlanetWebSocketHandler planetWebSocketHandler;
    private final ErrorReporterService errorReporterService;

    @Override
    public List<Future<ExecutionResult<Boolean>>> sendMessages() {
        List<WsSessionPlanetIdMapping> connectedUsers = planetWebSocketHandler.getConnectedUsers();

        messageProviders.forEach(planetMessageProvider -> planetMessageProvider.clearDisconnectedUserData(connectedUsers));

        return connectedUsers.stream()
            .map(mapping -> sendMessage(mapping.getSessionId(), mapping.getUserId(), mapping.getPlanetId()))
            .toList();
    }

    private Future<ExecutionResult<Boolean>> sendMessage(String sessionId, UUID userId, UUID planetId) {
        return executorServiceBean.asyncProcess(() -> {
            try {
                Map<String, Object> payload = messageProviders.stream()
                    .map(planetMessageProvider -> planetMessageProvider.getMessage(sessionId, userId, planetId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toMap(UpdateItem::getKey, UpdateItem::getValue));

                if (payload.isEmpty()) {
                    log.debug("No messages to send to user {} for planet {}", userId, planetId);
                    return false;
                }

                WebSocketEvent event = WebSocketEvent.builder()
                    .eventName(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED)
                    .payload(payload)
                    .build();
                planetWebSocketHandler.sendEventToSession(sessionId, event);

                return true;
            } catch (Exception e) {
                String errorMessage = String.format("Failed sending PlanetOverview update for user %s to planet %s", userId, planetId);
                log.error(errorMessage, e);
                errorReporterService.report(errorMessage, e);
                return false;
            }
        });
    }
}
