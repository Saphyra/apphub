package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CitizenEfficiencyCalculatorTest {
    private static final int ACTUAL_MORALE = 800;
    private static final Integer MORALE_EFFICIENCY_DROP_UNDER = 1000;
    private static final Double MIN_MORALE_EFFICIENCY = 0.25;
    private static final Integer SKILL_LEVEL = 10;
    private static final Double SKILL_LEVEL_MULTIPLIER = 1.5;

    @Mock
    private CitizenEfficiencyCalculator.CompetencyProperties competencyProperties;

    @InjectMocks
    private CitizenEfficiencyCalculator underTest;

    @Mock
    private Citizen citizen;

    @Mock
    private Skill skill;

    @Test
    public void calculateEfficiency() {
        given(citizen.getMorale()).willReturn(ACTUAL_MORALE);
        given(citizen.getSkills()).willReturn(CollectionUtils.singleValueMap(SkillType.AIMING, skill));
        given(skill.getLevel()).willReturn(SKILL_LEVEL);

        given(competencyProperties.getMoraleEfficiencyDropUnder()).willReturn(MORALE_EFFICIENCY_DROP_UNDER);
        given(competencyProperties.getMinMoraleEfficiency()).willReturn(MIN_MORALE_EFFICIENCY);
        given(competencyProperties.getSkillLevelMultiplier()).willReturn(SKILL_LEVEL_MULTIPLIER);

        double result = underTest.calculateEfficiency(citizen, SkillType.AIMING);

        assertThat(result).isEqualTo(MIN_MORALE_EFFICIENCY * ACTUAL_MORALE / MORALE_EFFICIENCY_DROP_UNDER * (1 + SKILL_LEVEL * SKILL_LEVEL_MULTIPLIER));
    }
}