package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.Data;

import java.util.List;

@Data
public class Production {
    private List<SurfaceType> placed;
    private Integer amount;
    private ConstructionRequirements constructionRequirements;
    private SkillType requiredSkill;
}
