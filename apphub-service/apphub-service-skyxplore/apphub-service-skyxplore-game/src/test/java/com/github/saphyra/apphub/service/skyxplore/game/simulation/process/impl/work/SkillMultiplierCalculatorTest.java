package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSkillProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkillMultiplierCalculatorTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer SKILL_LEVEL = 34;
    private static final Double SKILL_LEVEL_MULTIPLIER = 1.5d;

    @Mock
    private GameProperties properties;

    @InjectMocks
    private SkillMultiplierCalculator underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Skills skills;

    @Mock
    private Skill skill;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSkillProperties skillProperties;

    @Test
    void calculateSkillMultiplier() {
        given(gameData.getSkills()).willReturn(skills);
        given(skills.findByCitizenIdAndSkillType(CITIZEN_ID, SkillType.AIMING)).willReturn(skill);
        given(skill.getLevel()).willReturn(SKILL_LEVEL);
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSkill()).willReturn(skillProperties);
        given(skillProperties.getSkillLevelMultiplier()).willReturn(SKILL_LEVEL_MULTIPLIER);

        assertThat(underTest.calculateSkillMultiplier(gameData, CITIZEN_ID, SkillType.AIMING)).isEqualTo(1 + ((SKILL_LEVEL - 1) * SKILL_LEVEL_MULTIPLIER));
    }
}