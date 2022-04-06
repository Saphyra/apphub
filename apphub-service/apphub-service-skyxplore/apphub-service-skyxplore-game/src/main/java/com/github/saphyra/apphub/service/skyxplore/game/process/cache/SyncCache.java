package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;

import java.util.List;
import java.util.UUID;

//TODO unit test
public class SyncCache {
    private final MessageCache messageCache;
    private final GameItemCache gameItemCache;

    public SyncCache(ExecutorServiceBean executorServiceBean, GameDataProxy gameDataProxy) {
        this.messageCache = new MessageCache(executorServiceBean);
        this.gameItemCache = new GameItemCache(gameDataProxy);
    }

    public void saveAll(List<GameItem> gameItems) {
        gameItemCache.saveAll(gameItems);
    }

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
