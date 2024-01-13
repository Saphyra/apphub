package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetPopulationOverviewResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.PlanetPopulationOverviewQueryService;
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
public class PlanetPopulationMessageProvider implements PlanetMessageProvider {
    private final DateTimeUtil dateTimeUtil;
    private final GameProperties gameProperties;
    private final PlanetPopulationOverviewQueryService planetPopulationOverviewQueryService;

    private final Map<String, LastMessage<PlanetPopulationOverviewResponse>> lastMessages = new ConcurrentHashMap<>();

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
            log.debug("Last Planet Population status is still valid for user {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        PlanetPopulationOverviewResponse actualPayload = planetPopulationOverviewQueryService.getPopulationOverview(userId, planetId);
        PlanetPopulationOverviewResponse lastMessage = Optional.ofNullable(lastMessages.get(sessionId))
            .map(LastMessage::getPayload)
            .orElse(null);

        if (actualPayload.equals(lastMessage)) {
            log.debug("No Planet Population update necessary for userId {} on planet {}", userId, planetId);
            return Optional.empty();
        }

        LastMessage<PlanetPopulationOverviewResponse> latest = LastMessage.<PlanetPopulationOverviewResponse>builder()
            .payload(actualPayload)
            .sentAt(dateTimeUtil.getCurrentDateTime())
            .build();
        lastMessages.put(sessionId, latest);

        return Optional.of(new PlanetUpdateItem("population", actualPayload));
    }

    private boolean shouldSend(String sessionId) {
        LastMessage<?> lastMessage = lastMessages.get(sessionId);
        if (isNull(lastMessage)) {
            return true;
        }

        LocalDateTime lastMessageSentAt = lastMessage.getSentAt();
        long pollingIntervalNanos = gameProperties.getMessageDelay().getPlanetPopulation() * 1000000;
        LocalDateTime nextPollingTime = dateTimeUtil.getCurrentDateTime().minusNanos(pollingIntervalNanos);
        return lastMessageSentAt.isBefore(nextPollingTime);
    }
}
