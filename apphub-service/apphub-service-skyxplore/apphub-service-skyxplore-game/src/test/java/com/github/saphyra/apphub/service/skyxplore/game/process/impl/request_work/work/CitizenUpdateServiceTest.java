package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

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
public class CitizenUpdateServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final int WORK_POINTS = 24;
    private static final int EXPERIENCE_PER_LEVEL = 32;
    private static final int EXPERIENCE = 12;
    private static final int NEXT_LEVEL = 1;
    private static final int SKILL_LEVEL = 2;
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private CitizenUpdateService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Planet planet;

    @Mock
    private Citizen citizen;

    @Mock
    private Skill skill;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSkillProperties skillProperties;

    @Mock
    private GameData gameData;

    @Mock
    private Citizens citizens;

    @Mock
    private Skills skills;

    @Test
    void updateCitizen() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(gameData.getSkills()).willReturn(skills);
        given(skills.findByCitizenIdAndSkillType(CITIZEN_ID, SkillType.AIMING)).willReturn(skill);
        given(skill.getExperience()).willReturn(EXPERIENCE);
        given(skill.getNextLevel()).willReturn(NEXT_LEVEL);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSkill()).willReturn(skillProperties);
        given(skillProperties.getExperiencePerLevel()).willReturn(EXPERIENCE_PER_LEVEL);
        given(skill.getLevel()).willReturn(SKILL_LEVEL);
        given(gameData.getGameId()).willReturn(GAME_ID);

        given(planet.getOwner()).willReturn(USER_ID);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(planet.getOwner()).willReturn(USER_ID);

        underTest.updateCitizen(syncCache, gameData, PLANET_ID, CITIZEN_ID, WORK_POINTS, SkillType.AIMING);

        verify(citizen).reduceMorale(WORK_POINTS);
        verify(skill).increaseExperience(WORK_POINTS);
        verify(skill).increaseLevel();
        verify(skill).setExperience(EXPERIENCE - NEXT_LEVEL);
        verify(skill).setNextLevel(SKILL_LEVEL * EXPERIENCE_PER_LEVEL);

        verify(syncCache).citizenModified(USER_ID, citizen, skill);
    }
}