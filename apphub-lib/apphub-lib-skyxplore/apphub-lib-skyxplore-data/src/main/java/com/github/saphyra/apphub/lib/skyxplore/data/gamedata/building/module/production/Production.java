package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Production {
    private String resourceDataId;
    @Builder.Default
    private Integer amount = 1;
    private ConstructionRequirements constructionRequirements;
}
