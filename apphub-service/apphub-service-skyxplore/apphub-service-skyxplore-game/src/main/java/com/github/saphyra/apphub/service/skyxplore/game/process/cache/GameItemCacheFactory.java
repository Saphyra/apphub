package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class GameItemCacheFactory {
    private final GameDataProxy gameDataProxy;

    GameItemCache create() {
        return new GameItemCache(gameDataProxy);
    }
}
