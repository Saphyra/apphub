package com.github.saphyra.apphub.service.skyxplore.game.tick.cache;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

//TODO unit test
public class GameItemCache {
    private final Map<UUID, GameItem> items = new ConcurrentHashMap<>();
    private final List<BiWrapper<UUID, GameItemType>> deletedItems = new Vector<>();

    public void saveAll(List<GameItem> gameItems) {
        gameItems.forEach(this::save);
    }

    public void save(GameItem gameItem) {
        items.put(gameItem.getId(), gameItem);
    }

    public void delete(UUID id, GameItemType type) {
        items.remove(id);
        deletedItems.add(new BiWrapper<>(id, type));
    }

    public void process(GameDataProxy gameDataProxy) {
        if (!items.isEmpty()) {
            gameDataProxy.saveItems(items.values());
        }
        if (!deletedItems.isEmpty()) {
            gameDataProxy.deleteItems(deletedItems);
        }
    }
}
