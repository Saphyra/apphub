package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessageDelay {
    private Long planetSurface;
    private Long planetQueue;
    private Long planetStorage;
    private Long planetPopulation;
    private Long planetBuilding;
    private Long population;
    private Long constructionAreaBuildingModules;
}
