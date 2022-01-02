package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum QueueItemType {
    TERRAFORMATION(PriorityType.CONSTRUCTION),
    CONSTRUCTION(PriorityType.CONSTRUCTION);

    private final PriorityType priorityType;
}
