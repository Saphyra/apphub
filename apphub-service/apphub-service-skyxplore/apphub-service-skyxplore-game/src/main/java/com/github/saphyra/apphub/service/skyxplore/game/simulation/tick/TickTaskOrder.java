package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TickTaskOrder {
    SATIETY_DECREASE(0),
    PROCESS_SCHEDULER(1000),
    PROCESS_CLEANUP(10000),
    PROCESS_DELETION(100000),
    ;
    private final int order;
}
