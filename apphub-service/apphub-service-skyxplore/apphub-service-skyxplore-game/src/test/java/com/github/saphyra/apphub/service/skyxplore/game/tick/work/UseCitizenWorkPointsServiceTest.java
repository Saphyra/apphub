package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.SkillToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.GameItemCache;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.MessageCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UseCitizenWorkPointsServiceTest {
    private static final int EXPERIENCE_PER_LEVEL = 1000;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final int WORK_POINTS_USED = 200;
    private static final int INCREASED_SKILL_EXPERIENCE = 1100;
    private static final Integer EXPERIENCE_FOR_NEXT_LEVEL = 800;
    private static final Integer SKILL_LEVEL = 2;
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @Mock
    private TickCache tickCache;

    @Mock
    private CitizenToResponseConverter citizenToResponseConverter;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private CitizenToModelConverter citizenToModelConverter;

    @Mock
    private SkillToModelConverter skillToModelConverter;

    private UseCitizenWorkPointsService underTest;

    @Mock
    private Citizen citizen;

    @Mock
    private Skill skill;

    @Mock
    private TickCacheItem tickCacheItem;

    @Mock
    private GameItemCache gameItemCache;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private SkillModel skillModel;

    @Mock
    private MessageCache messageCache;

    @Mock
    private CitizenResponse citizenResponse;

    @Before
    public void setUp() {
        underTest = UseCitizenWorkPointsService.builder()
            .experiencePerLevel(EXPERIENCE_PER_LEVEL)
            .tickCache(tickCache)
            .citizenToModelConverter(citizenToModelConverter)
            .citizenToResponseConverter(citizenToResponseConverter)
            .messageSender(messageSender)
            .skillToModelConverter(skillToModelConverter)
            .build();
    }

    @Test
    public void useWorkPoints() {
        given(citizen.getSkills()).willReturn(CollectionUtils.singleValueMap(SkillType.AIMING, skill));
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        given(skill.getExperience()).willReturn(INCREASED_SKILL_EXPERIENCE);
        given(skill.getNextLevel()).willReturn(EXPERIENCE_FOR_NEXT_LEVEL);
        given(skill.getLevel()).willReturn(SKILL_LEVEL);

        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getGameItemCache()).willReturn(gameItemCache);
        given(tickCacheItem.getMessageCache()).willReturn(messageCache);

        given(citizenToModelConverter.convert(citizen, GAME_ID)).willReturn(citizenModel);
        given(skillToModelConverter.convert(skill, GAME_ID)).willReturn(skillModel);
        given(citizenToResponseConverter.convert(citizen)).willReturn(citizenResponse);

        underTest.useWorkPoints(GAME_ID, USER_ID, PLANET_ID, citizen, WORK_POINTS_USED, SkillType.AIMING);

        verify(citizen).reduceMorale(WORK_POINTS_USED);
        verify(skill).increaseExperience(WORK_POINTS_USED);
        verify(skill).increaseLevel(1);
        verify(skill).setExperience(INCREASED_SKILL_EXPERIENCE - EXPERIENCE_FOR_NEXT_LEVEL);
        verify(skill).setNextLevel(SKILL_LEVEL * EXPERIENCE_PER_LEVEL);

        verify(gameItemCache).save(citizenModel);
        verify(gameItemCache).save(skillModel);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(messageCache).add(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED), eq(CITIZEN_ID), argumentCaptor.capture());
        argumentCaptor.getValue().run();
        verify(messageSender).planetCitizenModified(USER_ID, PLANET_ID, citizenResponse);
    }
}