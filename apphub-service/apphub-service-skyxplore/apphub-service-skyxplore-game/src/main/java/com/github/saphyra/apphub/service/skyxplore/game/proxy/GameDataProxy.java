package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataProxy {
    private final SkyXploreSavedGameClient dataGameClient;
    private final LocaleProvider localeProvider;

    public String loadItem(UUID id, GameItemType type) {
        return dataGameClient.loadGameItem(id, type, localeProvider.getOrDefault());
    }

    public String loadChildren(UUID parent, GameItemType type) {
        return dataGameClient.loadChildrenOfGameItem(parent, type, localeProvider.getOrDefault());
    }

    public void saveItem(GameItem... model) {
        dataGameClient.saveGameData(Arrays.asList(model), localeProvider.getOrDefault());
    }

    public void deleteItem(UUID id, GameItemType type) {
        dataGameClient.deleteGameItem(id, type, localeProvider.getOrDefault());
    }
}