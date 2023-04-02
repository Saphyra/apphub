package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.Data;

@Data
public class SolarSystemProperties {
    private Range<Integer> solarSystemDistance;
    private Integer padding; //Distance from the edge of the universe
    private Range<Integer> planetOrbitRadius;
}
