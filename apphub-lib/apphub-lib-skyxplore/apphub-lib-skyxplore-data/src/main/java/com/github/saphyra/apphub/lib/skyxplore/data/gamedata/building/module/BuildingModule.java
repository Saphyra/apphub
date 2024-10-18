package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BuildingModule extends GameDataItem {
    private BuildingModuleCategory category;
    private ConstructionRequirements constructionRequirements;

    private List<SurfaceType> supportedSurfacesRestriction = new ArrayList<>();
}
