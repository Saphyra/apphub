package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CitizenFinderTest {
    private static final UUID EFFICIENT_CITIZEN_ID = UUID.randomUUID();
    private static final UUID ALLOCATED_CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @InjectMocks
    private CitizenFinder underTest;

    @Mock
    private Citizen inefficientCitizen;

    @Mock
    private Citizen efficientCitizen;

    @Mock
    private Citizen tiredCitizen;

    @Mock
    private Citizen allocatedCitizen;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private Citizens citizens;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Test
    public void findSuitableCitizen() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.getByLocation(LOCATION)).willReturn(List.of(inefficientCitizen, efficientCitizen, tiredCitizen, allocatedCitizen));
        given(inefficientCitizen.getMorale()).willReturn(1);
        given(efficientCitizen.getMorale()).willReturn(1);
        given(allocatedCitizen.getMorale()).willReturn(1);

        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(allocatedCitizen.getCitizenId()).willReturn(ALLOCATED_CITIZEN_ID);
        given(citizenAllocations.findByCitizenId(ALLOCATED_CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));

        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, inefficientCitizen, SkillType.DOCTORING)).willReturn(1d);
        given(citizenEfficiencyCalculator.calculateEfficiency(gameData, efficientCitizen, SkillType.DOCTORING)).willReturn(2d);

        given(efficientCitizen.getCitizenId()).willReturn(EFFICIENT_CITIZEN_ID);

        Optional<UUID> result = underTest.getSuitableCitizen(gameData, LOCATION, SkillType.DOCTORING);

        assertThat(result).contains(EFFICIENT_CITIZEN_ID);
    }
}