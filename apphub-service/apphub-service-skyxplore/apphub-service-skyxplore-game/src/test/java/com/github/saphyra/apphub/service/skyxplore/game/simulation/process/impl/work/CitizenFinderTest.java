package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
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

@ExtendWith(MockitoExtension.class)
class CitizenFinderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int REQUESTED_WORK_POINTS = 4532;
    private static final UUID ALLOCATED_CITIZEN_ID = UUID.randomUUID();
    private static final UUID TIRED_CITIZEN_ID = UUID.randomUUID();
    private static final UUID INEFFICIENT_CITIZEN_ID = UUID.randomUUID();
    private static final UUID EFFICIENT_CITIZEN_ID = UUID.randomUUID();
    private static final Integer MORALE = 23;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @InjectMocks
    private CitizenFinder underTest;

    @Mock
    private Citizen allocatedCitizen;

    @Mock
    private Citizen tiredCitizen;

    @Mock
    private Citizen inefficientCitizen;

    @Mock
    private Citizen efficientCitizen;

    @Mock
    private GameData gameData;

    @Mock
    private Citizens citizens;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Test
    void getSuitableCitizen() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(LOCATION)).willReturn(List.of(allocatedCitizen, tiredCitizen, inefficientCitizen, efficientCitizen));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(allocatedCitizen.getCitizenId()).willReturn(ALLOCATED_CITIZEN_ID);
        given(tiredCitizen.getCitizenId()).willReturn(TIRED_CITIZEN_ID);
        given(inefficientCitizen.getCitizenId()).willReturn(INEFFICIENT_CITIZEN_ID);
        given(efficientCitizen.getCitizenId()).willReturn(EFFICIENT_CITIZEN_ID);
        given(citizenAllocations.findByCitizenId(ALLOCATED_CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizenAllocations.findByCitizenId(TIRED_CITIZEN_ID)).willReturn(Optional.empty());
        given(citizenAllocations.findByCitizenId(INEFFICIENT_CITIZEN_ID)).willReturn(Optional.empty());
        given(citizenAllocations.findByCitizenId(EFFICIENT_CITIZEN_ID)).willReturn(Optional.empty());
        given(tiredCitizen.getMorale()).willReturn(MORALE);
        given(inefficientCitizen.getMorale()).willReturn(MORALE);
        given(efficientCitizen.getMorale()).willReturn(MORALE);
        given(citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, tiredCitizen, SkillType.AIMING, REQUESTED_WORK_POINTS)).willReturn(MORALE + 1);
        given(citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, inefficientCitizen, SkillType.AIMING, REQUESTED_WORK_POINTS)).willReturn(MORALE);
        given(citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, efficientCitizen, SkillType.AIMING, REQUESTED_WORK_POINTS)).willReturn(MORALE);

        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, inefficientCitizen, SkillType.AIMING)).willReturn(0.1);
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, efficientCitizen, SkillType.AIMING)).willReturn(0.2);

        Optional<UUID> result = underTest.getSuitableCitizen(gameData, LOCATION, SkillType.AIMING, REQUESTED_WORK_POINTS);

        assertThat(result).contains(EFFICIENT_CITIZEN_ID);
    }
}