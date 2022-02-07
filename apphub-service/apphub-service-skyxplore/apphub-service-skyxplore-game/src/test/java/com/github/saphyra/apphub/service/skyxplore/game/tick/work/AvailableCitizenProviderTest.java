package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AvailableCitizenProviderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CITIZEN_ID_1 = UUID.randomUUID();
    private static final UUID CITIZEN_ID_2 = UUID.randomUUID();
    private static final UUID CITIZEN_ID_3 = UUID.randomUUID();

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @InjectMocks
    private AvailableCitizenProvider underTest;

    @Mock
    private Citizen tiredCitizen;

    @Mock
    private Citizen unemployedCitizen;

    @Mock
    private Citizen employedCitizen;

    @Mock
    private Citizen alreadyWorkingCitizen;

    @Mock
    private Assignment assignment;

    @Mock
    private Assignment anotherAssignment;

    @Test
    public void findMostCapableUnemployedCitizen() {
        Map<UUID, Assignment> assignments = CollectionUtils.toMap(
            new BiWrapper<>(CITIZEN_ID_1, anotherAssignment),
            new BiWrapper<>(CITIZEN_ID_2, assignment)
        );

        given(tiredCitizen.getMorale()).willReturn(0);
        given(unemployedCitizen.getMorale()).willReturn(1);
        given(employedCitizen.getMorale()).willReturn(1);
        given(alreadyWorkingCitizen.getMorale()).willReturn(1);

        given(unemployedCitizen.getCitizenId()).willReturn(CITIZEN_ID_3);
        given(employedCitizen.getCitizenId()).willReturn(CITIZEN_ID_1);
        given(alreadyWorkingCitizen.getCitizenId()).willReturn(CITIZEN_ID_2);

        given(anotherAssignment.getLocation()).willReturn(UUID.randomUUID());
        given(assignment.getLocation()).willReturn(LOCATION);
        given(assignment.getWorkPointsLeft()).willReturn(1);

        given(citizenEfficiencyCalculator.calculateEfficiency(unemployedCitizen, SkillType.AIMING)).willReturn(1d);
        given(citizenEfficiencyCalculator.calculateEfficiency(alreadyWorkingCitizen, SkillType.AIMING)).willReturn(2d);

        Optional<Citizen> result = underTest.findMostCapableUnemployedCitizen(assignments, List.of(tiredCitizen, unemployedCitizen, employedCitizen, alreadyWorkingCitizen), LOCATION, SkillType.AIMING);

        assertThat(result).contains(alreadyWorkingCitizen);
    }
}