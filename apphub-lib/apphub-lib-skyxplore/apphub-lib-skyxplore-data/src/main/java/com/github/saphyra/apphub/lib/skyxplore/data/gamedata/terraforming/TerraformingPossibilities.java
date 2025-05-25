package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class TerraformingPossibilities extends ArrayList<TerraformingPossibility> {
    public TerraformingPossibilities(List<TerraformingPossibility> terraformingPossibilities) {
        addAll(terraformingPossibilities);
    }

    //TODO unit test
    public TerraformingPossibility findBySurfaceType(SurfaceType surfaceType) {
        return stream()
            .filter(terraformingPossibility -> terraformingPossibility.getSurfaceType() == surfaceType)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("This surfaceType cannot be terraformed to " + surfaceType));
    }
}
