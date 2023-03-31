package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreSavedGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.GameToGameItemListConverter;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO move to creation
public class GameSaverService {
    private final GameToGameItemListConverter converter;
    private final GameProperties gameProperties;
    private final SkyXploreSavedGameClient gameClient;
    private final CustomLocaleProvider customLocaleProvider;

    public void save(Game game) {
        try {
            List<GameItem> items = converter.convertDeep(game);
            log.info("Number of gameItems to save: {}", items.size());
            Lists.partition(items, gameProperties.getItemSaverMaxChunkSize())
                .forEach(items1 -> gameClient.saveGameData(items1, customLocaleProvider.getLocale()));
        } catch (Exception e) {
            log.error("Exception", e);
            throw e;
        }
    }
}
