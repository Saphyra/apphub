package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class RestProcessHelper {
    private final GameProperties gameProperties;
    private final CitizenAllocationFactory citizenAllocationFactory;
    private final CitizenAllocationConverter citizenAllocationConverter;

    double getMoraleBasedMultiplier(GameData gameData, UUID citizenId) {
        CitizenMoraleProperties moraleProperties = gameProperties.getCitizen()
            .getMorale();

        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);

        double result = (double) moraleProperties.getWorkEfficiencyDropUnder() / citizen.getMorale();

        log.info("Morale Priority multiplier is {}, when the citizen has {} morale.", result, citizen.getMorale());

        return result;
    }

    void allocateCitizen(SyncCache syncCache, GameData gameData, UUID processId, UUID citizenId) {
        CitizenAllocation citizenAllocation = citizenAllocationFactory.create(citizenId, processId);

        gameData.getCitizenAllocations()
            .add(citizenAllocation);
        syncCache.saveGameItem(citizenAllocationConverter.toModel(gameData.getGameId(), citizenAllocation));
    }

    void releaseCitizen(SyncCache syncCache, GameData gameData, UUID processId) {
        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);
                syncCache.deleteGameItem(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }

    void increaseMorale(SyncCache syncCache, GameData gameData, UUID citizenId) {
        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);

        int moraleRegen = gameProperties.getCitizen()
            .getMorale()
            .getRegenPerTick();

        citizen.increaseMorale(moraleRegen);

        syncCache.citizenModified(citizen);
    }
}
