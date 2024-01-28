package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameConverter;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class GameSaverService {
    private final GameConverter converter;
    private final GameDataProxy gameDataProxy;

    void save(Game game) {
        try {
            List<GameItem> items = converter.convertDeep(game);
            log.info("Number of gameItems to save: {}", items.size());
            gameDataProxy.saveItems(items);
        } catch (Exception e) {
            log.error("Exception", e);
            throw e;
        }
    }
}
