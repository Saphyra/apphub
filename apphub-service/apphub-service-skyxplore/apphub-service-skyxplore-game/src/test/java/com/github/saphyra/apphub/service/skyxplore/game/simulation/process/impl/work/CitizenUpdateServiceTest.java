package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSkillProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
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
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenEfficiencyCalculator citizenEfficiencyCalculator;

    @InjectMocks
    private CitizenUpdateService underTest;

    @Mock
    private SyncCache syncCache;

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
    private Planet planet;

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
        given(citizen.getLocation()).willReturn(LOCATION);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);

        underTest.updateCitizen(syncCache, gameData, CITIZEN_ID, WORK_POINTS, SkillType.AIMING);

        verify(skill).increaseExperience(MORALE_USED);
        verify(skill).increaseLevel();
        verify(skill).setExperience(EXPERIENCE - NEXT_LEVEL);
        verify(skill).setNextLevel(LEVEL * EXPERIENCE_PER_LEVEL);
        verify(syncCache).citizenExperienceEarned(OWNER_ID, citizen, skill);
    }
}