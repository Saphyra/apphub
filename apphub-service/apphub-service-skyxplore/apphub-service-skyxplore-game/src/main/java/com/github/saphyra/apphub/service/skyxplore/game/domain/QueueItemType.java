package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QueueItemType {
    CONSTRUCT_CONSTRUCTION_AREA(PriorityType.CONSTRUCTION),
    DECONSTRUCT_CONSTRUCTION_AREA(PriorityType.CONSTRUCTION),
    TERRAFORMATION(PriorityType.CONSTRUCTION),
    @Deprecated(forRemoval = true)
    CONSTRUCTION(PriorityType.CONSTRUCTION),
    @Deprecated(forRemoval = true)
    DECONSTRUCTION(PriorityType.CONSTRUCTION),
    ;

    private final PriorityType priorityType;
}
