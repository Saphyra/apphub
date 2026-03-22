package com.github.saphyra.apphub.service.feature.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.Game;

public interface TickTask {
    TickTaskOrder getOrder();

    default boolean shouldProcess(Game game) {
        return true;
    }

    void process(Game game);
}
