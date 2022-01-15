package com.github.saphyra.apphub.service.skyxplore.game.tick;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ProcessTickService {
    void processTick(Game game) {
        log.info("Processing tick for game {}", game.getGameId());
    }
}
