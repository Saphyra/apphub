package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.construction_area;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.MessageSender;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;
import com.github.saphyra.apphub.service.skyxplore.game.ws.planet.SkyXploreGameConstructionAreaWebSocketHandler;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Component
@RequiredArgsConstructor
@Slf4j
public class ConstructionAreaMessageSender implements MessageSender {
    private final SkyXploreGameConstructionAreaWebSocketHandler constructionAreaWebSocketHandler;
    private final List<ConstructionAreaMessageProvider> messageProviders;
    private final ExecutorServiceBean executorServiceBean;
    private final ErrorReporterService errorReporterService;

    @Override
    public List<FutureWrapper<Boolean>> sendMessages() {
        List<WsSessionConstructionAreaIdMapping> connectedUsers = constructionAreaWebSocketHandler.getConnectedUsers();

        messageProviders.forEach(planetMessageProvider -> planetMessageProvider.clearDisconnectedUserData(connectedUsers));

        return connectedUsers.stream()
            .map(mapping -> sendMessage(mapping.getSessionId(), mapping.getUserId(), mapping.getConstructionAreaId()))
            .toList();
    }

    private FutureWrapper<Boolean> sendMessage(String sessionId, UUID userId, UUID constructionAreaId) {
        return executorServiceBean.asyncProcess(() -> {
            try {
                Map<String, Object> payload = messageProviders.stream()
                    .map(messageProvider -> messageProvider.getMessage(sessionId, userId, constructionAreaId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toMap(UpdateItem::getKey, UpdateItem::getValue));

                if (payload.isEmpty()) {
                    log.debug("No messages to send to user {} for constructionArea {}", userId, constructionAreaId);
                    return false;
                }

                WebSocketEvent event = WebSocketEvent.builder()
                    .eventName(WebSocketEventName.SKYXPLORE_GAME_CONSTRUCTION_AREA_MODIFIED)
                    .payload(payload)
                    .build();
                constructionAreaWebSocketHandler.sendEventToSession(sessionId, event);

                return true;
            } catch (Exception e) {
                String errorMessage = String.format("Failed sending ConstructionArea update for user %s to ConstructionArea %s", userId, constructionAreaId);
                errorReporterService.report(errorMessage, e);
                return false;
            }
        });
    }
}
