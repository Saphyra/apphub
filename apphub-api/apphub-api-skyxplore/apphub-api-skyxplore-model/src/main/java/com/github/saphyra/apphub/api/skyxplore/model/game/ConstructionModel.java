package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConstructionModel extends GameItem {
    private UUID externalReference;
    private ConstructionType constructionType; //TODO save to database
    private Integer parallelWorkers;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
    private Integer priority;
    private String data;
}
