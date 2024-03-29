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
    private final ConstructionType constructionType;
    private final UUID location;
    private final Integer requiredWorkPoints;
    private final String data;
    private Integer currentWorkPoints;
    private Integer priority;

    public void increaseCurrentWorkPoints(int completedWorkPoints) {
        this.currentWorkPoints += completedWorkPoints;
    }
}
