package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataProxy {
    private final SkyXploreSavedGameClient dataGameClient;
    private final LocaleProvider localeProvider;

    public void saveItem(GameItem... model) {
        saveItems(Arrays.asList(model));
    }

    public void saveItems(Collection<GameItem> model) {
        dataGameClient.saveGameData(new ArrayList<>(model), localeProvider.getOrDefault());
    }

    public void deleteItem(UUID id, GameItemType type) {
        deleteItems(List.of(new BiWrapper<>(id, type)));
    }

    public void deleteItem(List<GameItem> items) {
        deleteItems(
            items.stream()
                .map(gameItem -> new BiWrapper<>(gameItem.getId(), gameItem.getType()))
                .collect(Collectors.toList())
        );
    }

    public void deleteItems(List<BiWrapper<UUID, GameItemType>> items) {
        dataGameClient.deleteGameItem(items, localeProvider.getOrDefault());
    }

    public GameModel getGameModel(UUID gameId) {
        return dataGameClient.getGameModel(gameId, localeProvider.getOrDefault());
    }
}
