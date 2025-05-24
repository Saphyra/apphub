package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class WorkProcessHelper {
    private final ProductionBuildingFinder productionBuildingFinder;
    private final BuildingModuleAllocationFactory buildingModuleAllocationFactory;
    private final BuildingModuleAllocationConverter buildingAllocationConverter;
    private final CitizenFinder citizenFinder;
    private final CitizenAllocationFactory citizenAllocationFactory;
    private final CitizenAllocationConverter citizenAllocationConverter;
    private final GameProperties gameProperties;
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;
    private final CitizenUpdateService citizenUpdateService;

    @Deprecated(forRemoval = true)
    public void allocateParentAsBuildingIfPossible(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID externalReference) {
        if (gameData.getBuildingModuleAllocations().getByBuildingModuleId(externalReference).isEmpty()) {
            createAndSaveBuildingAllocation(progressDiff, gameData, processId, externalReference);
        } else {
            log.debug("Someone is already working on {}", processId);
        }
    }

    @Deprecated(forRemoval = true)
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

    @Deprecated(forRemoval = true)
    private void createAndSaveBuildingAllocation(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID buildingId) {
        BuildingModuleAllocation buildingModuleAllocation = buildingModuleAllocationFactory.create(buildingId, processId);

        gameData.getBuildingModuleAllocations()
            .add(buildingModuleAllocation);

        progressDiff.save(buildingAllocationConverter.toModel(gameData.getGameId(), buildingModuleAllocation));
    }

    @Deprecated(forRemoval = true)
    void allocateCitizenIfPossible(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, SkillType requiredSkill, int requestedWorkPoints) {
        citizenFinder.getSuitableCitizen(gameData, location, requiredSkill, requestedWorkPoints)
            .ifPresent(citizenId -> {
                CitizenAllocation citizenAllocation = citizenAllocationFactory.create(citizenId, processId);

                gameData.getCitizenAllocations()
                    .add(citizenAllocation);

                progressDiff.save(citizenAllocationConverter.toModel(gameData.getGameId(), citizenAllocation));
            });
    }

    int work(GameProgressDiff progressDiff, GameData gameData, UUID processId, SkillType skillType, int workPointsLeft) {
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

        return completedWorkPoints;
    }

    @Deprecated(forRemoval = true)
    public void releaseBuildingAndCitizen(GameProgressDiff progressDiff, GameData gameData, UUID processId) {
        gameData.getBuildingModuleAllocations()
            .findByProcessId(processId)
            .ifPresent(allocation -> {
                gameData.getBuildingModuleAllocations()
                    .remove(allocation);

                progressDiff.delete(allocation.getBuildingModuleAllocationId(), GameItemType.BUILDING_MODULE_ALLOCATION);
            });

        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);
                progressDiff.delete(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }

    public boolean tryAllocateCitizen(GameProgressDiff progressDiff, GameData gameData, UUID location, UUID processId, SkillType skillType) {
        Optional<UUID> maybeAvailableCitizen = getBestSuitableCitizen(gameData, location, skillType);

        if (maybeAvailableCitizen.isPresent()) {
            citizenAllocationFactory.save(progressDiff, gameData, maybeAvailableCitizen.get(), processId);

            return true;
        }

        return false;
    }

    private Optional<UUID> getBestSuitableCitizen(GameData gameData, UUID location, SkillType skillType) {
        return gameData.getCitizens()
            .getByLocation(location)
            .stream()
            .filter(citizen -> gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isEmpty())
            .map(citizen -> new BiWrapper<>(citizen.getCitizenId(), citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, skillType)))
            .max(Comparator.comparing(BiWrapper::getEntity2))
            .map(BiWrapper::getEntity1);
    }

    public void releaseCitizen(GameProgressDiff progressDiff, GameData gameData, UUID processId) {
        gameData.getCitizenAllocations()
            .findByProcessId(processId)
            .ifPresent(citizenAllocation -> {
                gameData.getCitizenAllocations()
                    .remove(citizenAllocation);
                progressDiff.delete(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
            });
    }
}
