package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

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
    private final int parallelWorkers;
    private final int requiredWorkPoints;
    private final String data;
    private int currentWorkPoints;
    private int priority;
}
