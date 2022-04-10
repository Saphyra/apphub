package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SyncCache {
    @NonNull
    private final MessageCache messageCache;

    @NonNull
    private final GameItemCache gameItemCache;

    public void saveGameItem(GameItem gameItem) {
        gameItemCache.save(gameItem);
    }

    public void deleteGameItem(UUID id, GameItemType type) {
        gameItemCache.delete(id, type);
    }

    public void addMessage(UUID recipient, WebSocketEventName eventName, Object id, Runnable method) {
        messageCache.add(recipient, eventName, id, method);
    }

    public void process() {
        messageCache.process();
        gameItemCache.process();
    }
}
