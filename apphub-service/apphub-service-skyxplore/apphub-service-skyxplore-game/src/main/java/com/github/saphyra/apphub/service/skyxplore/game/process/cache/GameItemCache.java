package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class GameItemCache {
    private final Map<UUID, GameItem> items = new ConcurrentHashMap<>();
    private final List<BiWrapper<UUID, GameItemType>> deletedItems = new Vector<>();

    private final GameDataProxy gameDataProxy;

    public void saveAll(List<GameItem> gameItems) {
        gameItems.forEach(this::save);
    }

    public void save(GameItem gameItem) {
        log.debug("Saving {} to cache", gameItem);
        items.put(gameItem.getId(), gameItem);
    }

    public void delete(UUID id, GameItemType type) {
        items.remove(id);
        deletedItems.add(new BiWrapper<>(id, type));
    }

    public void process() {
        log.debug("Saving {} number of gameItems", items.size());

        if (!items.isEmpty()) {
            gameDataProxy.saveItems(items.values());
        }

        log.debug("Deleting {} number of gameItems", deletedItems.size());
        if (!deletedItems.isEmpty()) {
            gameDataProxy.deleteItems(deletedItems);
        }
    }
}
