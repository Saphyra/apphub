package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production;

import com.github.saphyra.apphub.lib.common_util.map.OptionalHashMap;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductionBuilding extends BuildingData {
    private OptionalHashMap<String, ProductionData> gives;
    private Integer workers;
    private SurfaceType primarySurfaceType;

    @Override
    public List<SurfaceType> getPlaceableSurfaceTypes() {
        return gives.values().stream()
            .flatMap(production -> production.getPlaced().stream())
            .collect(Collectors.toList());
    }

    @Override
    public SurfaceType getPrimarySurfaceType() {
        if (isNull(primarySurfaceType)) {
            primarySurfaceType = getPlaceableSurfaceTypes().get(0);
        }
        return primarySurfaceType;
    }
}
