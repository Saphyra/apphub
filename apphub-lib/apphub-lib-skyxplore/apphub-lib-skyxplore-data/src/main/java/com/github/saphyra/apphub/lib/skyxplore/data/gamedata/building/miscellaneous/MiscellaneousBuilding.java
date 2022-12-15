package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.miscellaneous;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MiscellaneousBuilding extends BuildingData {
    private List<SurfaceType> placeableSurfaceTypes;

    @Override
    public SurfaceType getPrimarySurfaceType() {
        return getPlaceableSurfaceTypes().get(0);
    }
}
