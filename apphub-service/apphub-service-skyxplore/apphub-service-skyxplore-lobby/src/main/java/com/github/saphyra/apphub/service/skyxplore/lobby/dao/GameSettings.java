package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameSettings {
    private UniverseSize universeSize = UniverseSize.SMALL;
    private SystemSize systemSize = SystemSize.MEDIUM;
    private PlanetSize planetSize = PlanetSize.MEDIUM;
    private AiPresence aiPresence = AiPresence.RARE;
}
