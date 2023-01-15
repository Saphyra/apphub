package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSkillProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SkillToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
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
    private SkillToModelConverter skillToModelConverter;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenToModelConverter citizenToModelConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private CitizenToResponseConverter citizenToResponseConverter;

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
    private SkillModel skillModel;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private CitizenResponse citizenResponse;

    @Test
    public void updateCitizen() {
        given(planet.getPopulation()).willReturn(CollectionUtils.singleValueMap(CITIZEN_ID, citizen));
        given(citizen.getSkills()).willReturn(CollectionUtils.singleValueMap(SkillType.AIMING, skill));
        given(skill.getExperience()).willReturn(EXPERIENCE);
        given(skill.getNextLevel()).willReturn(NEXT_LEVEL);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSkill()).willReturn(skillProperties);
        given(skillProperties.getExperiencePerLevel()).willReturn(EXPERIENCE_PER_LEVEL);
        given(skill.getLevel()).willReturn(SKILL_LEVEL);

        given(skillToModelConverter.convert(skill, GAME_ID)).willReturn(skillModel);
        given(citizenToModelConverter.convert(citizen, GAME_ID)).willReturn(citizenModel);
        given(citizenToResponseConverter.convert(citizen)).willReturn(citizenResponse);

        given(planet.getOwner()).willReturn(USER_ID);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);

        underTest.updateCitizen(syncCache, GAME_ID, planet, CITIZEN_ID, WORK_POINTS, SkillType.AIMING);

        verify(citizen).reduceMorale(WORK_POINTS);
        verify(skill).increaseExperience(WORK_POINTS);
        verify(skill).increaseLevel();
        verify(skill).setExperience(EXPERIENCE - NEXT_LEVEL);
        verify(skill).setNextLevel(SKILL_LEVEL * EXPERIENCE_PER_LEVEL);
        verify(syncCache).saveGameItem(citizenModel);
        verify(syncCache).saveGameItem(skillModel);
        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED), eq(CITIZEN_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetCitizenModified(USER_ID, PLANET_ID, citizenResponse);
    }
}