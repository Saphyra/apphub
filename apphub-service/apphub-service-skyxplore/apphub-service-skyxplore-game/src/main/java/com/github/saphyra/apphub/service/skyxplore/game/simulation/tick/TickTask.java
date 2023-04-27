package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;

public interface TickTask {
    TickTaskOrder getOrder();

    void process(Game game, SyncCache syncCache);
}
