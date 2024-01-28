package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target.UpdateTargetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class WorkProcessHelper {
    private final ProductionBuildingFinder productionBuildingFinder;
    private final BuildingAllocationFactory buildingAllocationFactory;
    private final BuildingAllocationConverter buildingAllocationConverter;
    private final CitizenFinder citizenFinder;
    private final CitizenAllocationFactory citizenAllocationFactory;
    private final CitizenAllocationConverter citizenAllocationConverter;
    private final GameProperties gameProperties;
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;
    private final CitizenUpdateService citizenUpdateService;
    private final UpdateTargetService updateTargetService;

    public void allocateParentAsBuildingIfPossible(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID externalReference) {
        if (gameData.getBuildingAllocations().getByBuildingId(externalReference).isEmpty()) {
            createAndSaveBuildingAllocation(progressDiff, gameData, processId, externalReference);
        } else {
            log.debug("Someone is already working on {}", processId);
        }
    }

    void allocateBuildingIfPossible(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, String buildingDataId) {
        productionBuildingFinder.findSuitableProductionBuilding(gameData, location, buildingDataId)
            .ifPresentOrElse(
                buildingId -> {
                    log.info("No suitable {} found at {}", buildingDataId, location);

                    createAndSaveBuildingAllocation(progressDiff, gameData, processId, buildingId);
                },
                () -> log.info("No suitable {} found at {}", buildingDataId, location)
            );
    }

    private void createAndSaveBuildingAllocation(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID buildingId) {
        BuildingAllocation buildingAllocation = buildingAllocationFactory.create(buildingId, processId);

        gameData.getBuildingAllocations()
            .add(buildingAllocation);

        progressDiff.save(buildingAllocationConverter.toModel(gameData.getGameId(), buildingAllocation));
    }

    void allocateCitizenIfPossible(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, SkillType requiredSkill, int requestedWorkPoints) {
        citizenFinder.getSuitableCitizen(gameData, location, requiredSkill, requestedWorkPoints)
            .ifPresent(citizenId -> {
                CitizenAllocation citizenAllocation = citizenAllocationFactory.create(citizenId, processId);

                gameData.getCitizenAllocations()
                    .add(citizenAllocation);

                progressDiff.save(citizenAllocationConverter.toModel(gameData.getGameId(), citizenAllocation));
            });
    }

    public int work(GameProgressDiff progressDiff, GameData gameData, UUID processId, SkillType skillType, int workPointsLeft, WorkProcessType processType, UUID targetId) {
        int defaultWorkPointsPerTick = gameProperties.getCitizen()
            .getWorkPointsPerTick();

        UUID citizenId = gameData.getCitizenAllocations()
            .findByProcessIdValidated(processId)
            .getCitizenId();
        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);
        double citizenEfficiency = citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, skillType);
        int maxWorkPoints = (int) Math.floor(citizenEfficiency * defaultWorkPointsPerTick);
        int completedWorkPoints = Math.min(maxWorkPoints, workPointsLeft);

        citizenUpdateService.updateCitizen(progressDiff, gameData, citizenId, completedWorkPoints, skillType);

        updateTargetService.updateTarget(progressDiff, gameData, processType, targetId, completedWorkPoints);

        return completedWorkPoints;
    }

    public void releaseBuildingAndCitizen(GameProgressDiff progressDiff, GameData gameData, UUID processId) {
        gameData.getBuildingAllocations()
            .findByProcessId(processId)
            .ifPresent(allocation -> {
                gameData.getBuildingAllocations()
                    .remove(allocation);

                progressDiff.delete(allocation.getBuildingAllocationId(), GameItemType.BUILDING_ALLOCATION);
            });

        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);
                progressDiff.delete(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }
}
