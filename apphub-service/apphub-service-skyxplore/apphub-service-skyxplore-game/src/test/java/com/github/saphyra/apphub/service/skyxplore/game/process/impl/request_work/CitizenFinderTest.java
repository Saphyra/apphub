package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CitizenFinderTest {
    private static final UUID CITIZEN_ID_1 = UUID.randomUUID();
    private static final UUID CITIZEN_ID_2 = UUID.randomUUID();
    private static final UUID CITIZEN_ID_3 = UUID.randomUUID();
    private static final UUID CITIZEN_ID_4 = UUID.randomUUID();

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @InjectMocks
    private CitizenFinder underTest;

    @Mock
    private Planet planet;

    @Mock
    private Citizen inefficientCitizen;

    @Mock
    private Citizen efficientCitizen;

    @Mock
    private Citizen tiredCitizen;

    @Mock
    private Citizen allocatedCitizen;

    @Test
    public void findSuitableCitizen() {
        given(planet.getPopulation()).willReturn(CollectionUtils.toMap(
            new BiWrapper<>(CITIZEN_ID_1, inefficientCitizen),
            new BiWrapper<>(CITIZEN_ID_2, efficientCitizen),
            new BiWrapper<>(CITIZEN_ID_3, tiredCitizen),
            new BiWrapper<>(CITIZEN_ID_4, allocatedCitizen)
        ));
        given(inefficientCitizen.getMorale()).willReturn(1);
        given(efficientCitizen.getMorale()).willReturn(1);
        given(allocatedCitizen.getMorale()).willReturn(1);
        given(planet.getCitizenAllocations()).willReturn(new CitizenAllocations(CollectionUtils.singleValueMap(CITIZEN_ID_4, UUID.randomUUID())));

        given(citizenEfficiencyCalculator.calculateEfficiency(inefficientCitizen, SkillType.DOCTORING)).willReturn(1d);
        given(citizenEfficiencyCalculator.calculateEfficiency(efficientCitizen, SkillType.DOCTORING)).willReturn(2d);

        given(efficientCitizen.getCitizenId()).willReturn(CITIZEN_ID_2);

        Optional<UUID> result = underTest.getSuitableCitizen(planet, SkillType.DOCTORING);

        assertThat(result).contains(CITIZEN_ID_2);
    }
}