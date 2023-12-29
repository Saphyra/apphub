package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.overview.PlanetStorageOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.planet.WsSessionPlanetMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
public class PlanetStorageMessageProvider implements PlanetMessageProvider {
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
    private final PlanetStorageOverviewQueryService planetStorageOverviewQueryService;

    private final Map<String, LastMessage<PlanetStorageResponse>> lastMessages = new ConcurrentHashMap<>();

    @Override
    public void clearDisconnectedUserData(List<WsSessionPlanetMapping> connectedUsers) {
        List<String> connectedSessions = connectedUsers.stream()
            .map(WsSessionPlanetMapping::getSessionId)
            .toList();

        List<String> entriesToRemove = lastMessages.keySet()
            .stream()
            .filter(sessionId -> !connectedSessions.contains(sessionId))
            .toList();

        entriesToRemove.forEach(lastMessages::remove);
    }

    @Override
    public Optional<PlanetUpdateItem> getMessage(String sessionId, UUID userId, UUID planetId) {
        if (!shouldSend(sessionId)) {
            log.debug("Last PlanetQueue status is still valid for user {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        PlanetStorageResponse actualPayload = planetStorageOverviewQueryService.getStorage(userId, planetId);
        PlanetStorageResponse lastMessage = Optional.ofNullable(lastMessages.get(sessionId))
            .map(LastMessage::getPayload)
            .orElse(null);

        if (actualPayload.equals(lastMessage)) {
            log.debug("No Planet Storage update necessary for userId {} on planet {}", userId, planetId);
            return Optional.empty();
        }


        LastMessage<PlanetStorageResponse> latest = LastMessage.<PlanetStorageResponse>builder()
            .payload(actualPayload)
            .sentAt(dateTimeUtil.getCurrentDateTime())
            .build();
        lastMessages.put(sessionId, latest);

        return Optional.of(new PlanetUpdateItem("storage", actualPayload));
    }

    private boolean shouldSend(String sessionId) {
        LastMessage<?> lastMessage = lastMessages.get(sessionId);
        if (isNull(lastMessage)) {
            return true;
        }

        return lastMessage.getSentAt().isBefore(dateTimeUtil.getCurrentDateTime().minusNanos(gameProperties.getMessageDelay().getPlanetStorage() * 1000));
    }
}
