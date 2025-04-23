package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QueueItemType {
    CONSTRUCT_BUILDING_MODULE(PriorityType.CONSTRUCTION),
    DECONSTRUCT_BUILDING_MODULE(PriorityType.CONSTRUCTION),
    CONSTRUCT_CONSTRUCTION_AREA(PriorityType.CONSTRUCTION),
    DECONSTRUCT_CONSTRUCTION_AREA(PriorityType.CONSTRUCTION),
    TERRAFORMATION(PriorityType.CONSTRUCTION),
    ;

    private final PriorityType priorityType;
}
