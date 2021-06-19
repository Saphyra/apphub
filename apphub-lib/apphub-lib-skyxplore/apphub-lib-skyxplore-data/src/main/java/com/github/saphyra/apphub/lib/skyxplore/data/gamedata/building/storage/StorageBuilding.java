package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StorageBuilding extends BuildingData {
    private StorageType stores;
    private Integer capacity;

    @Override
    public List<SurfaceType> getPlaceableSurfaceTypes() {
        return Arrays.asList(SurfaceType.CONCRETE);
    }

    @Override
    public SurfaceType getPrimarySurfaceType() {
        return SurfaceType.CONCRETE;
    }
}
