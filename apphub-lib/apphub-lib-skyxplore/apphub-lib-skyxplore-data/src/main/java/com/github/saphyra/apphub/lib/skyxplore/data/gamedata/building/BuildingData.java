package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class BuildingData extends GameDataItem {
    private String buildingType;
    private boolean defaultBuilding = false;
    private Map<Integer, ConstructionRequirements> constructionRequirements;

    public abstract List<SurfaceType> getPlaceableSurfaceTypes();

    public abstract SurfaceType getPrimarySurfaceType();
}
