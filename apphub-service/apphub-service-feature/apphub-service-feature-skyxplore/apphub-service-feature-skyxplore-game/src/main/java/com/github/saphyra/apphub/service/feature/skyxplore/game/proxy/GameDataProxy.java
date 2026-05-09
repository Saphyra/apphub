package com.github.saphyra.apphub.service.feature.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.feature.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.feature.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.skyxplore.game.config.properties.GameProperties;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataProxy {
    private final SkyXploreSavedGameClient dataGameClient;
    private final GameProperties gameProperties;

    public void saveItem(GameItem... model) {
        saveItems(Arrays.asList(model));
    }

    public void saveItems(List<GameItem> items) {
        Lists.partition(items, gameProperties.getItemSaverMaxChunkSize())
            .forEach(dataGameClient::saveGameData);
    }

    public void deleteItem(UUID id, GameItemType type) {
        deleteItems(List.of(new BiWrapper<>(id, type)));
    }

    public void deleteItems(List<BiWrapper<UUID, GameItemType>> items) {
        dataGameClient.deleteGameItem(items);
    }

    public GameModel getGameModel(UUID gameId) {
        log.info("Loading GameModel with gameId {}", gameId);
        return dataGameClient.getGameModel(gameId);
    }
}
