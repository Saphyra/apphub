package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSkillProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CitizenEfficiencyCalculatorTest {
    private static final int ACTUAL_MORALE = 800;
    private static final Integer MORALE_EFFICIENCY_DROP_UNDER = 1000;
    private static final Double MIN_MORALE_EFFICIENCY = 0.25;
    private static final Integer SKILL_LEVEL = 2;
    private static final Double SKILL_LEVEL_MULTIPLIER = 0.1;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private CitizenEfficiencyCalculator underTest;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSkillProperties skillProperties;

    @Mock
    private CitizenMoraleProperties moraleProperties;

    @Mock
    private Citizen citizen;

    @Mock
    private Skill skill;

    @Test
    public void calculateEfficiency() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);

        given(citizenProperties.getSkill()).willReturn(skillProperties);
        given(skillProperties.getSkillLevelMultiplier()).willReturn(SKILL_LEVEL_MULTIPLIER);

        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getWorkEfficiencyDropUnder()).willReturn(MORALE_EFFICIENCY_DROP_UNDER);
        given(moraleProperties.getMinEfficiency()).willReturn(MIN_MORALE_EFFICIENCY);

        given(citizen.getMorale()).willReturn(ACTUAL_MORALE);
        given(citizen.getSkills()).willReturn(CollectionUtils.singleValueMap(SkillType.AIMING, skill));
        given(skill.getLevel()).willReturn(SKILL_LEVEL);


        double result = underTest.calculateEfficiency(citizen, SkillType.AIMING);

        assertThat(result).isEqualTo(0.8 * 1.1);
    }
}