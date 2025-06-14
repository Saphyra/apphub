package com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction;

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
    private final UUID location;
    private final Integer requiredWorkPoints;
    private final String data;
    private Integer priority;
}
