package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public class SurfaceProperties {
    private List<SurfaceTypeSpawnDetails> spawnDetails;
    private Map<SurfaceType, Integer> logisticsWeight;

    public void validate() {
        Arrays.stream(SurfaceType.values())
            .forEach(surfaceType -> {
                if (!logisticsWeight.containsKey(surfaceType)) {
                    throw new IllegalStateException("Unconfigured logisticsWeight for surfaceType " + surfaceType);
                }
            });
    }
}
