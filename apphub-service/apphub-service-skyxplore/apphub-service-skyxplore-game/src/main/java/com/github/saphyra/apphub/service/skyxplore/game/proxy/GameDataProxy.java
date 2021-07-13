package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
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
    private final LocaleProvider localeProvider;

    public GameItem loadItem(UUID id, GameItemType type) {
        return dataGameClient.loadGameItem(id, type, localeProvider.getOrDefault());
    }

    public List<GameItem> loadChildren(UUID parent, GameItemType type) {
        return dataGameClient.loadChildrenOfGameItem(parent, type, localeProvider.getOrDefault());
    }

    public void saveListItem(GameItem... model) {
        dataGameClient.saveGameData(Arrays.asList(model), localeProvider.getOrDefault());
    }
}
