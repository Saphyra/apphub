package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Construction {
    private final UUID constructionId;
    private final UUID externalReference;
    private final ConstructionType type; //TODO map to model
    private final UUID location; //TODO map to model
    private final int parallelWorkers;
    private final int requiredWorkPoints;
    private final String data;
    private int currentWorkPoints;
    private int priority;
}
