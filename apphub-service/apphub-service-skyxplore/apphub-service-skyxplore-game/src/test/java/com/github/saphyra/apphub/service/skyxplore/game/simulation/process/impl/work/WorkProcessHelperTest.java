package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingModuleAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingModuleAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_allocation.BuildingAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target.UpdateTargetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final int REQUESTED_WORK_POINTS = 243;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final int WORK_POINTS_LEFT = 320;
    private static final Integer WORK_POINTS_PER_TICK = 50;
    private static final Double CITIZEN_EFFICIENCY = 2d;
    private static final UUID BUILDING_ALLOCATION_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingFinder productionBuildingFinder;

    @Mock
    private BuildingAllocationFactory buildingAllocationFactory;

    @Mock
    private BuildingAllocationConverter buildingAllocationConverter;

    @Mock
    private CitizenFinder citizenFinder;

    @Mock
    private CitizenAllocationFactory citizenAllocationFactory;

    @Mock
    private CitizenAllocationConverter citizenAllocationConverter;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @Mock
    private CitizenUpdateService citizenUpdateService;

    @Mock
    private UpdateTargetService updateTargetService;

    @InjectMocks
    private WorkProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private BuildingModuleAllocations buildingModuleAllocations;

    @Mock
    private BuildingModuleAllocation buildingModuleAllocation;

    @Mock
    private BuildingModuleAllocationModel buildingModuleAllocationModel;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private CitizenAllocationModel citizenAllocationModel;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private Citizens citizens;

    @Mock
    private Citizen citizen;

    @Test
    void allocateParentAsBuildingIfPossible_alreadyAllocated() {
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocations.getByBuildingModuleId(EXTERNAL_REFERENCE)).willReturn(List.of(buildingModuleAllocation));

        underTest.allocateParentAsBuildingIfPossible(progressDiff, gameData, PROCESS_ID, EXTERNAL_REFERENCE);

        verifyNoInteractions(buildingAllocationFactory);
    }

    @Test
    void allocateParentAsBuildingIfPossible() {
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocations.getByBuildingModuleId(EXTERNAL_REFERENCE)).willReturn(Collections.emptyList());
        given(buildingAllocationFactory.create(EXTERNAL_REFERENCE, PROCESS_ID)).willReturn(buildingModuleAllocation);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(buildingAllocationConverter.toModel(GAME_ID, buildingModuleAllocation)).willReturn(buildingModuleAllocationModel);

        underTest.allocateParentAsBuildingIfPossible(progressDiff, gameData, PROCESS_ID, EXTERNAL_REFERENCE);

        verify(buildingModuleAllocations).add(buildingModuleAllocation);
        verify(progressDiff).save(buildingModuleAllocationModel);
    }

    @Test
    void allocateBuildingIfPossible() {
        given(productionBuildingFinder.findSuitableProductionBuilding(gameData, LOCATION, BUILDING_DATA_ID)).willReturn(Optional.of(BUILDING_ID));
        given(buildingAllocationFactory.create(BUILDING_ID, PROCESS_ID)).willReturn(buildingModuleAllocation);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(buildingAllocationConverter.toModel(GAME_ID, buildingModuleAllocation)).willReturn(buildingModuleAllocationModel);
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);

        underTest.allocateBuildingIfPossible(progressDiff, gameData, PROCESS_ID, LOCATION, BUILDING_DATA_ID);

        verify(buildingModuleAllocations).add(buildingModuleAllocation);
        verify(progressDiff).save(buildingModuleAllocationModel);
    }

    @Test
    void allocateCitizenIfPossible() {
        given(citizenFinder.getSuitableCitizen(gameData, LOCATION, SkillType.AIMING, REQUESTED_WORK_POINTS)).willReturn(Optional.of(CITIZEN_ID));
        given(citizenAllocationFactory.create(CITIZEN_ID, PROCESS_ID)).willReturn(citizenAllocation);
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(citizenAllocationConverter.toModel(GAME_ID, citizenAllocation)).willReturn(citizenAllocationModel);

        underTest.allocateCitizenIfPossible(progressDiff, gameData, PROCESS_ID, LOCATION, SkillType.AIMING, REQUESTED_WORK_POINTS);

        verify(citizenAllocations).add(citizenAllocation);
        verify(progressDiff).save(citizenAllocationModel);
    }

    @Test
    void work() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getWorkPointsPerTick()).willReturn(WORK_POINTS_PER_TICK);
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessIdValidated(PROCESS_ID)).willReturn(citizenAllocation);
        given(citizenAllocation.getCitizenId()).willReturn(CITIZEN_ID);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, SkillType.AIMING)).willReturn(CITIZEN_EFFICIENCY);

        int result = underTest.work(progressDiff, gameData, PROCESS_ID, SkillType.AIMING, WORK_POINTS_LEFT, WorkProcessType.CONSTRUCTION, TARGET_ID);

        int completedWorkPoints = (int) (CITIZEN_EFFICIENCY * WORK_POINTS_PER_TICK);
        assertThat(result).isEqualTo(completedWorkPoints);

        verify(citizenUpdateService).updateCitizen(progressDiff, gameData, CITIZEN_ID, completedWorkPoints, SkillType.AIMING);
        verify(updateTargetService).updateTarget(progressDiff, gameData, WorkProcessType.CONSTRUCTION, TARGET_ID, completedWorkPoints);
    }

    @Test
    void releaseBuildingAndCitizen() {
        given(gameData.getBuildingModuleAllocations()).willReturn(buildingModuleAllocations);
        given(buildingModuleAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(buildingModuleAllocation));
        given(buildingModuleAllocation.getBuildingAllocationId()).willReturn(BUILDING_ALLOCATION_ID);

        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizenAllocation.getCitizenAllocationId()).willReturn(CITIZEN_ALLOCATION_ID);

        underTest.releaseBuildingAndCitizen(progressDiff, gameData, PROCESS_ID);

        verify(buildingModuleAllocations).remove(buildingModuleAllocation);
        verify(progressDiff).delete(BUILDING_ALLOCATION_ID, GameItemType.BUILDING_ALLOCATION);

        verify(citizenAllocations).remove(citizenAllocation);
        verify(progressDiff).delete(CITIZEN_ALLOCATION_ID, GameItemType.CITIZEN_ALLOCATION);
    }
}