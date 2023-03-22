package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.Data;

@Data
public class PlanetProperties {
    private Range<Double> orbitSpeed;
    private Integer defaultRawFoodAmount;
}
