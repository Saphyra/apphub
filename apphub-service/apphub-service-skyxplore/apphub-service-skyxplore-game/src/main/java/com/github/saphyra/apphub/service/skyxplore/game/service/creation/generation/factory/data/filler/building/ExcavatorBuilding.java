package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;

import java.util.Arrays;
import java.util.List;

class ExcavatorBuilding extends BuildingData {
    private final SurfaceType surfaceType;

    ExcavatorBuilding(SurfaceType surfaceType) {
        setId(GameConstants.DATA_ID_EXCAVATOR);
        this.surfaceType = surfaceType;
    }

    @Override
    public List<SurfaceType> getPlaceableSurfaceTypes() {
        return Arrays.asList(surfaceType);
    }

    @Override
    public SurfaceType getPrimarySurfaceType() {
        return surfaceType;
    }
}
