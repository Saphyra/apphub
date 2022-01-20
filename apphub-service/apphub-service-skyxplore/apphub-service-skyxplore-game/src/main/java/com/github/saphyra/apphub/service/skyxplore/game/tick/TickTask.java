package com.github.saphyra.apphub.service.skyxplore.game.tick;

public interface TickTask {
    int getPriority();

    void process();
}
