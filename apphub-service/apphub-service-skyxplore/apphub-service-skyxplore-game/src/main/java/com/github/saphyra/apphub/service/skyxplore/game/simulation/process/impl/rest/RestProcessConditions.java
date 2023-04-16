package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class RestProcessConditions {
    boolean citizenAllocated(GameData gameData, UUID citizenId) {
        return gameData.getCitizenAllocations()
            .findByCitizenId(citizenId)
            .isPresent();
    }
}
