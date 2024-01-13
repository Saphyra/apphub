package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetBuildingOverviewResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.overview.PlanetBuildingOverviewQueryService;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
public class PlanetBuildingMessageProvider implements PlanetMessageProvider {
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
    private final PlanetBuildingOverviewQueryService planetBuildingOverviewQueryService;

    private final Map<String, LastMessage<Map<String, PlanetBuildingOverviewResponse>>> lastMessages = new ConcurrentHashMap<>();

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

    @Override
    public Optional<PlanetUpdateItem> getMessage(String sessionId, UUID userId, UUID planetId) {
        if (!shouldSend(sessionId)) {
            log.debug("Last Planet Building status is still valid for user {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        Map<String, PlanetBuildingOverviewResponse> actualPayload = planetBuildingOverviewQueryService.getBuildingOverview(userId, planetId);
        Map<String, PlanetBuildingOverviewResponse> lastMessage = Optional.ofNullable(lastMessages.get(sessionId))
            .map(LastMessage::getPayload)
            .orElse(null);

        if (actualPayload.equals(lastMessage)) {
            log.debug("No Planet Building update necessary for userId {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        LastMessage<Map<String, PlanetBuildingOverviewResponse>> latest = LastMessage.<Map<String, PlanetBuildingOverviewResponse>>builder()
            .payload(actualPayload)
            .sentAt(dateTimeUtil.getCurrentDateTime())
            .build();
        lastMessages.put(sessionId, latest);

        return Optional.of(new PlanetUpdateItem("buildings", actualPayload));
    }

    private boolean shouldSend(String sessionId) {
        LastMessage<?> lastMessage = lastMessages.get(sessionId);
        if (isNull(lastMessage)) {
            return true;
        }

        LocalDateTime lastMessageSentAt = lastMessage.getSentAt();
        long pollingIntervalNanos = gameProperties.getMessageDelay().getPlanetBuilding() * 1000000;
        LocalDateTime nextPollingTime = dateTimeUtil.getCurrentDateTime().minusNanos(pollingIntervalNanos);
        return lastMessageSentAt.isBefore(nextPollingTime);
    }
}
