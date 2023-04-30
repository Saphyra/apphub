package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QueueItemType {
    TERRAFORMATION(PriorityType.CONSTRUCTION),
    CONSTRUCTION(PriorityType.CONSTRUCTION),
    DECONSTRUCTION(PriorityType.CONSTRUCTION),
    ;

    private final PriorityType priorityType;
}
