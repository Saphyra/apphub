package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.construction_area;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.LastMessage;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.util.MessageSenderUtil;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Slf4j
@Builder
@AllArgsConstructor
public class DefaultConstructionAreaMessageProvider<T> implements ConstructionAreaMessageProvider {
    @NonNull
    private final MessageSenderUtil messageSenderUtil;

    @NonNull
    private final String itemKey;

    @NonNull
    private final BiFunction<UUID, UUID, T> responseProvider;

    @NonNull
    private final DateTimeUtil dateTimeUtil;

    @NonNull
    private final Long pollingInterval;

    @Builder.Default
    private final Map<String, LastMessage<T>> lastMessages = new ConcurrentHashMap<>();

    @Override
    public void clearDisconnectedUserData(List<WsSessionConstructionAreaIdMapping> connectedUsers) {
        messageSenderUtil.clearDisconnectedUserData(connectedUsers, lastMessages);
    }

    @Override
    public Optional<UpdateItem> getMessage(String sessionId, UUID userId, UUID planetId) {
        if (messageSenderUtil.lastMessageValid(sessionId, lastMessages, pollingInterval)) {
            log.debug("Last Planet {} status is still valid for user {} on planet {}", itemKey, userId, planetId);
            return Optional.empty();
        }

        T actualPayload = responseProvider.apply(userId, planetId);
        T lastMessage = Optional.ofNullable(lastMessages.get(sessionId))
            .map(LastMessage::getPayload)
            .orElse(null);

        if (actualPayload.equals(lastMessage)) {
            log.debug("No Planet {} update necessary for userId {} on planet {}", itemKey, userId, planetId);
            return Optional.empty();
        }

        LastMessage<T> latest = LastMessage.<T>builder()
            .payload(actualPayload)
            .sentAt(dateTimeUtil.getCurrentDateTime())
            .build();
        lastMessages.put(sessionId, latest);

        return Optional.of(new UpdateItem(itemKey, actualPayload));
    }
}
