package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.Data;

import java.util.Map;

@Data
public class PlanetProperties {
    private Map<SystemSize, Range<Integer>> planetsPerSystem;
    private Map<PlanetSize, Range<Integer>> planetSize;
}
