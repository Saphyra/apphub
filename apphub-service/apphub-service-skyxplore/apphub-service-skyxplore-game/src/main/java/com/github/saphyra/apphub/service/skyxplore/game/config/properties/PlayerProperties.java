package com.github.saphyra.apphub.service.skyxplore.game.config.properties;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.lib.common_domain.Range;
import lombok.Data;

import java.util.Map;

@Data
public class PlayerProperties {
    private Map<AiPresence, Range<Double>> aiSpawnChance;
}
