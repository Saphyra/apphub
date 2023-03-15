package com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
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
    private final ProcessType type;
    private int value;
}
