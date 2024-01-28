package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSenderUtil {
    private final DateTimeUtil dateTimeUtil;

    public <T> void clearDisconnectedUserData(List<WsSessionPlanetIdMapping> connectedUsers, Map<String, LastMessage<T>> lastMessages) {
        List<String> connectedSessions = connectedUsers.stream()
            .map(WsSessionPlanetIdMapping::getSessionId)
            .toList();

        List<String> entriesToRemove = lastMessages.keySet()
            .stream()
            .filter(sessionId -> !connectedSessions.contains(sessionId))
            .toList();

        entriesToRemove.forEach(lastMessages::remove);
    }

    public <T> boolean lastMessageValid(String sessionId, Map<String, LastMessage<T>> lastMessages, long pollingInterval) {
        LastMessage<?> lastMessage = lastMessages.get(sessionId);
        if (isNull(lastMessage)) {
            return false;
        }

        LocalDateTime lastMessageSentAt = lastMessage.getSentAt();
        long pollingIntervalNanos = pollingInterval * 1_000_000;
        LocalDateTime nextPollingTime = lastMessageSentAt.plusNanos(pollingIntervalNanos);
        return dateTimeUtil.getCurrentDateTime().isBefore(nextPollingTime);
    }
}
