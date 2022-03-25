package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import lombok.Data;

@Data
public class SurfaceTypeSpawnDetails {
    private String surfaceName;
    private Integer spawnRate;
    private boolean optional = false;
}
