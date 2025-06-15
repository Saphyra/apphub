package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSkillProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CitizenUpdateServiceTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final int WORK_POINTS = 432;
    private static final Integer MORALE_USED = 324;
    private static final Integer EXPERIENCE = 324;
    private static final Integer NEXT_LEVEL = 235;
    private static final Integer EXPERIENCE_PER_LEVEL = 2356;
    private static final Integer LEVEL = 567;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @Mock
    private CitizenConverter citizenConverter;

    @Mock
    private SkillConverter skillConverter;

    @InjectMocks
    private CitizenUpdateService underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Citizens citizens;

    @Mock
    private Citizen citizen;

    @Mock
    private Skills skills;

    @Mock
    private Skill skill;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSkillProperties skillProperties;

    @Mock
    private SkillModel skillModel;

    @Mock
    private CitizenModel citizenModel;

    @Test
    void updateCitizen() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, citizen, SkillType.AIMING, WORK_POINTS)).willReturn(MORALE_USED);
        given(gameData.getSkills()).willReturn(skills);
        given(skills.findByCitizenIdAndSkillType(CITIZEN_ID, SkillType.AIMING)).willReturn(skill);
        given(skill.getExperience()).willReturn(EXPERIENCE);
        given(skill.getNextLevel()).willReturn(NEXT_LEVEL);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSkill()).willReturn(skillProperties);
        given(skillProperties.getExperiencePerLevel()).willReturn(EXPERIENCE_PER_LEVEL);
        given(skill.getLevel()).willReturn(LEVEL);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(citizenConverter.toModel(GAME_ID, citizen)).willReturn(citizenModel);
        given(skillConverter.toModel(GAME_ID, skill)).willReturn(skillModel);

        underTest.updateCitizen(progressDiff, gameData, CITIZEN_ID, WORK_POINTS, SkillType.AIMING);

        verify(skill).increaseExperience(MORALE_USED);
        verify(skill).increaseLevel();
        verify(skill).setExperience(EXPERIENCE - NEXT_LEVEL);
        verify(skill).setNextLevel(LEVEL * EXPERIENCE_PER_LEVEL);
        then(progressDiff).should().save(citizenModel);
        then(progressDiff).should().save(skillModel);
    }
}