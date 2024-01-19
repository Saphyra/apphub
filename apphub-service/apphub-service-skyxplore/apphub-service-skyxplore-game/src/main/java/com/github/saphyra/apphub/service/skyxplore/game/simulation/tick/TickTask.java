package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;

public interface TickTask {
    TickTaskOrder getOrder();

    void process(Game game);
}
