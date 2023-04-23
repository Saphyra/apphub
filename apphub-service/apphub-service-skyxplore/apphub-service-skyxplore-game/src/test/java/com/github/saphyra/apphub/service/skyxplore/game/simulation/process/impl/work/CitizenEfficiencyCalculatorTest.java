package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CitizenEfficiencyCalculatorTest {
    private static final Integer CITIZEN_MORALE = 500;
    private static final Double MORALE_MULTIPLIER = 1.5;
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Double SKILL_MULTIPLIER = 0.7;
    private static final int REQUESTED_WORK_POINTS = 1000;

    @Mock
    private MoraleMultiplierCalculator moraleMultiplierCalculator;

    @Mock
    private SkillMultiplierCalculator skillMultiplierCalculator;

    @InjectMocks
    private CitizenEfficiencyCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Citizen citizen;

    @Test
    void calculateEfficiency() {
        given(citizen.getMorale()).willReturn(CITIZEN_MORALE);
        given(moraleMultiplierCalculator.calculateMoraleMultiplier(CITIZEN_MORALE)).willReturn(MORALE_MULTIPLIER);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(skillMultiplierCalculator.calculateSkillMultiplier(gameData, CITIZEN_ID, SkillType.AIMING)).willReturn(SKILL_MULTIPLIER);

        double result = underTest.calculateEfficiency(gameData, citizen, SkillType.AIMING);

        assertThat(result).isEqualTo(MORALE_MULTIPLIER * SKILL_MULTIPLIER);
    }

    @Test
    void calculateMoraleRequirement() {
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(skillMultiplierCalculator.calculateSkillMultiplier(gameData, CITIZEN_ID, SkillType.AIMING)).willReturn(SKILL_MULTIPLIER);

        assertThat(underTest.calculateMoraleRequirement(gameData, citizen, SkillType.AIMING, REQUESTED_WORK_POINTS)).isEqualTo((int) Math.ceil(REQUESTED_WORK_POINTS / SKILL_MULTIPLIER));
    }
}