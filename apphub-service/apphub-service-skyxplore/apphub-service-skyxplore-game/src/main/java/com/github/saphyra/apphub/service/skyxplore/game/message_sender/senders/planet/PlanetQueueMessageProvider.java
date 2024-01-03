package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.queue.QueueFacade;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
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

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetQueueMessageProvider implements PlanetMessageProvider {
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
    private final QueueFacade queueFacade;

    private final Map<String, LastMessage<List<QueueResponse>>> lastMessages = new ConcurrentHashMap<>();

    @Override
    public void clearDisconnectedUserData(List<WsSessionPlanetIdMapping> connectedUsers) {
        List<String> connectedSessions = connectedUsers.stream()
            .map(WsSessionPlanetIdMapping::getSessionId)
            .toList();

        List<String> entriesToRemove = lastMessages.keySet()
            .stream()
            .filter(sessionId -> !connectedSessions.contains(sessionId))
            .toList();

        entriesToRemove.forEach(lastMessages::remove);
    }

    private boolean shouldSend(String sessionId) {
        LastMessage<?> lastMessage = lastMessages.get(sessionId);
        if (isNull(lastMessage)) {
            return true;
        }

        return lastMessage.getSentAt().isBefore(dateTimeUtil.getCurrentDateTime().minusNanos(gameProperties.getMessageDelay().getPlanetQueue() * 1000));
    }

    @Override
    public Optional<PlanetUpdateItem> getMessage(String sessionId, UUID userId, UUID planetId) {
        if (!shouldSend(sessionId)) {
            log.debug("Last PlanetQueue status is still valid for user {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        List<QueueResponse> actualPayload = queueFacade.getQueueOfPlanet(userId, planetId);
        List<QueueResponse> lastMessage = Optional.ofNullable(lastMessages.get(sessionId))
            .map(LastMessage::getPayload)
            .orElse(Collections.emptyList());

        if (actualPayload.size() == lastMessage.size() && new HashSet<>(actualPayload).containsAll(lastMessage)) {
            log.debug("No Planet Queue update necessary for userId {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        LastMessage<List<QueueResponse>> latest = LastMessage.<List<QueueResponse>>builder()
            .payload(actualPayload)
            .sentAt(dateTimeUtil.getCurrentDateTime())
            .build();
        lastMessages.put(sessionId, latest);

        return Optional.of(new PlanetUpdateItem("queue", actualPayload));
    }
}
