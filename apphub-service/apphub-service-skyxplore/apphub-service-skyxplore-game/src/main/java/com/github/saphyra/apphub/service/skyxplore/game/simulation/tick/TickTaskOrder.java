package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TickTaskOrder {
    PLAYER_CONNECTION_CHECKER(0),
    SATIETY_DECREASE(0),
    STORAGE_SETTING(0),
    PROCESS_SCHEDULER(1000),
    REST_SCHEDULER(10000),
    PROCESS_DELETION(100000),
    ;
    private final int order;
}
