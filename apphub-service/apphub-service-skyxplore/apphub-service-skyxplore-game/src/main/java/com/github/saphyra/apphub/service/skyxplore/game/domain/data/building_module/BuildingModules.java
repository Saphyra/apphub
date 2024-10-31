package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

//TODO unit test
public class BuildingModules extends Vector<BuildingModule> {
    public List<BuildingModule> getByLocation(UUID location) {
        return stream()
            .filter(buildingModule -> buildingModule.getLocation().equals(location))
            .toList();
    }

    public List<BuildingModule> getByLocationAndDataId(UUID location, String dataId) {
        return getByLocation(location)
            .stream()
            .filter(buildingModule -> buildingModule.getDataId().equals(dataId))
            .toList();
    }
}
