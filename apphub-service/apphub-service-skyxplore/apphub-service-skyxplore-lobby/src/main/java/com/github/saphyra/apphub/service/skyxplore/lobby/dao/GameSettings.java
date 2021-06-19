package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.PlanetSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemAmount;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.SystemSize;
import com.github.saphyra.apphub.api.skyxplore.model.game_setting.UniverseSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSettings {
    private UniverseSize universeSize = UniverseSize.SMALL;
    private SystemAmount systemAmount = SystemAmount.MEDIUM;
    private SystemSize systemSize = SystemSize.MEDIUM;
    private PlanetSize planetSize = PlanetSize.MEDIUM;
    private AiPresence aiPresence = AiPresence.EVERYWHERE; //reset to RARE as default
}
