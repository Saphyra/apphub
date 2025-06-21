package com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeconstructionFactory {
    private final IdGenerator idGenerator;

    public Deconstruction create(UUID externalReference, UUID location) {
        return Deconstruction.builder()
            .deconstructionId(idGenerator.randomUuid())
            .externalReference(externalReference)
            .location(location)
            .priority(GameConstants.DEFAULT_PRIORITY)
            .build();
    }
}
