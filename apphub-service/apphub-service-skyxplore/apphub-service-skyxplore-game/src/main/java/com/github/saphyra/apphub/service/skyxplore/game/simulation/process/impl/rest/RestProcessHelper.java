package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
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
    private final CitizenConverter citizenConverter;

    double getMoraleBasedMultiplier(GameData gameData, UUID citizenId) {
        CitizenMoraleProperties moraleProperties = gameProperties.getCitizen()
            .getMorale();

        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);

        double result = (double) moraleProperties.getWorkEfficiencyDropUnder() / citizen.getMorale();

        log.debug("Morale Priority multiplier is {}, when the citizen has {} morale.", result, citizen.getMorale());

        return result;
    }

    void allocateCitizen(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID citizenId) {
        CitizenAllocation citizenAllocation = citizenAllocationFactory.create(citizenId, processId);

        gameData.getCitizenAllocations()
            .add(citizenAllocation);
        progressDiff.save(citizenAllocationConverter.toModel(gameData.getGameId(), citizenAllocation));
    }

    void releaseCitizen(GameProgressDiff progressDiff, GameData gameData, UUID processId) {
        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);
                progressDiff.delete(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }

    void increaseMorale(GameProgressDiff progressDiff, GameData gameData, UUID citizenId) {
        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);

        int moraleRegen = gameProperties.getCitizen()
            .getMorale()
            .getRegenPerTick();

        citizen.increaseMorale(moraleRegen);

        progressDiff.save(citizenConverter.toModel(gameData.getGameId(), citizen));
    }
}
