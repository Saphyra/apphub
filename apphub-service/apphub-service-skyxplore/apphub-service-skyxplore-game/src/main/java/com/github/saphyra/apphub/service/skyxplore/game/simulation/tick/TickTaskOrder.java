package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TickTaskOrder {
    SATIETY_DECREASE(0),
    ;
    private final int order;
}
