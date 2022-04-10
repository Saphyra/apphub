package com.github.saphyra.apphub.service.skyxplore.game.process.background;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackgroundProcessFactory {
    private final ProcessContext processContext;

    public BackgroundProcesses create(Game game) {
        return new BackgroundProcesses(game, processContext);
    }
}
