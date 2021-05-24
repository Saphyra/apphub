package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.Data;

@Data
public class TerraformingPossibility {
    private SurfaceType surfaceType;
    private ConstructionRequirements constructionRequirements;
}
