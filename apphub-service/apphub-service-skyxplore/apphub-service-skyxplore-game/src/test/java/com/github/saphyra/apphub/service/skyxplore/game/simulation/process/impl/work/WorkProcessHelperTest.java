package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class WorkProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final int WORK_POINTS_LEFT = 10;
    private static final int WORK_POINTS_PER_TICK = 5;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Double CITIZEN_EFFICIENCY = 0.8;
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();

    @Mock
    private CitizenAllocationFactory citizenAllocationFactory;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @Mock
    private CitizenUpdateService citizenUpdateService;

    @InjectMocks
    private WorkProcessHelper underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private Citizens citizens;

    @Mock
    private Citizen citizen;

    @Test
    void work() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getWorkPointsPerTick()).willReturn(WORK_POINTS_PER_TICK);
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessIdValidated(PROCESS_ID)).willReturn(citizenAllocation);
        given(citizenAllocation.getCitizenId()).willReturn(CITIZEN_ID);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, SkillType.MINING)).willReturn(CITIZEN_EFFICIENCY);

        assertThat(underTest.work(progressDiff, gameData, PROCESS_ID, SkillType.MINING, WORK_POINTS_LEFT)).isEqualTo(4);

        then(citizenUpdateService).should().updateCitizen(progressDiff, gameData, CITIZEN_ID, 4, SkillType.MINING);
    }

    @Test
    void tryAllocateCitizen_nooAvailableCitizen() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(LOCATION)).willReturn(List.of(citizen));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));

        assertThat(underTest.tryAllocateCitizen(progressDiff, gameData, LOCATION, PROCESS_ID, SkillType.MINING)).isFalse();

        then(citizenAllocationFactory).shouldHaveNoInteractions();
    }

    @Test
    void tryAllocateCitizen_allocate() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(LOCATION)).willReturn(List.of(citizen));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.empty());
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, SkillType.MINING)).willReturn(CITIZEN_EFFICIENCY);
        given(citizenAllocationFactory.save(progressDiff, gameData, CITIZEN_ID, PROCESS_ID)).willReturn(citizenAllocation);

        assertThat(underTest.tryAllocateCitizen(progressDiff, gameData, LOCATION, PROCESS_ID, SkillType.MINING)).isTrue();

        then(citizenAllocationFactory).should().save(progressDiff, gameData, CITIZEN_ID, PROCESS_ID);
    }

    @Test
    void releaseCitizen() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizenAllocation.getCitizenAllocationId()).willReturn(CITIZEN_ALLOCATION_ID);

        underTest.releaseCitizen(progressDiff, gameData, PROCESS_ID);

        then(citizenAllocations).should().remove(citizenAllocation);
        then(progressDiff).should().delete(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);
    }
}