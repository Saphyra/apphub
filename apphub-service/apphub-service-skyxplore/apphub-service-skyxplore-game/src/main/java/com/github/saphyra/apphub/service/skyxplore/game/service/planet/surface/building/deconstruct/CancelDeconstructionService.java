package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.deconstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CancelDeconstructionService {
    public void cancelDeconstruction(UUID userId, UUID planetId, UUID deconstructionId) {
        //TODO implement
    }
}
