package com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Priority {
    private final UUID priorityId;
    private final UUID location;
    private final PriorityType type;
    private int value;
}
