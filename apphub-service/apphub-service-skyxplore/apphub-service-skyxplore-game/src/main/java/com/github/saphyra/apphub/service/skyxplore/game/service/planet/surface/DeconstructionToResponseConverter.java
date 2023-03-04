package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.DeconstructionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionToResponseConverter {
    private final GameProperties gameProperties;

    public DeconstructionResponse convert(Deconstruction deconstruction) {
        return DeconstructionResponse.builder()
            .deconstructionId(deconstruction.getDeconstructionId())
            .requiredWorkPoints(gameProperties.getDeconstruction().getRequiredWorkPoints())
            .currentWorkPoints(deconstruction.getCurrentWorkPoints())
            .build();
    }
}
