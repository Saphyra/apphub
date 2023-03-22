package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.building;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;

import java.util.Arrays;
import java.util.List;

class ExcavatorBuilding extends BuildingData {
    private static final String EXCAVATOR_ID = "excavator";

    private final SurfaceType surfaceType;

    ExcavatorBuilding(SurfaceType surfaceType) {
        setId(EXCAVATOR_ID);
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
