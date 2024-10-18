package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConstructionArea extends GameDataItem {
    private List<SurfaceType> supportedSurfaces;
    private Map<BuildingModuleCategory, Integer> slots;
    private ConstructionRequirements constructionRequirements;
}
