package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.Data;

import java.util.Map;

@Data
public class SolarSystemProperties {
    private Map<UniverseSize, Double> solarSystemDistanceMultiplier;
    private Range<Integer> solarSystemDistance;
    private int minPlanetDistance;
    private Map<SystemAmount, Range<Double>> amountMultiplier;
    private Map<SystemSize, Range<Integer>> radius;
}
