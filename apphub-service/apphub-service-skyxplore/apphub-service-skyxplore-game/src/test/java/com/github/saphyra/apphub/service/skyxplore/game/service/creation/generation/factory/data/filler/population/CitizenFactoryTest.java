package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenHitPointsProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSatietyProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durabilities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durability;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.RandomNameProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CitizenFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer MORALE = 3124;
    private static final Integer SATIETY = 32;
    private static final String NAME = "name";
    private static final Integer BASE_HIT_POINTS = 3245;
    private static final Integer HIT_POINTS_PER_LEVEL = 235;
    private static final UUID DURABILITY_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RandomNameProvider randomNameProvider;

    @Mock
    private SkillFactory skillFactory;

    @Mock
    private GameProperties properties;

    @InjectMocks
    private CitizenFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties citizenMoraleProperties;

    @Mock
    private CitizenSatietyProperties citizenSatietyProperties;

    @Mock
    private Citizens citizens;

    @Mock
    private Skill skill;

    @Mock
    private Skills skills;

    @Mock
    private CitizenHitPointsProperties citizenHitPointsProperties;

    @Mock
    private Durabilities durabilities;

    @Test
    void create() {
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(idGenerator.randomUuid())
            .willReturn(CITIZEN_ID)
            .willReturn(DURABILITY_ID);
        given(citizenProperties.getMorale()).willReturn(citizenMoraleProperties);
        given(citizenMoraleProperties.getMax()).willReturn(MORALE);
        given(citizenProperties.getSatiety()).willReturn(citizenSatietyProperties);
        given(citizenSatietyProperties.getMax()).willReturn(SATIETY);
        given(gameData.getCitizens()).willReturn(citizens);
        given(randomNameProvider.getRandomName(Collections.emptyList())).willReturn(NAME);
        given(skillFactory.create(any(), eq(CITIZEN_ID))).willReturn(skill);
        given(gameData.getSkills()).willReturn(skills);
        given(citizenProperties.getHitPoints()).willReturn(citizenHitPointsProperties);
        given(citizenHitPointsProperties.getBase()).willReturn(BASE_HIT_POINTS);
        given(citizenHitPointsProperties.getPerLevel()).willReturn(HIT_POINTS_PER_LEVEL);
        given(gameData.getDurabilities()).willReturn(durabilities);

        underTest.addToGameData(LOCATION, gameData);

        ArgumentCaptor<Citizen> citizenArgumentCaptor = ArgumentCaptor.forClass(Citizen.class);
        verify(citizens).add(citizenArgumentCaptor.capture());
        Citizen citizen = citizenArgumentCaptor.getValue();
        assertThat(citizen.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(citizen.getName()).isEqualTo(NAME);
        assertThat(citizen.getLocation()).isEqualTo(LOCATION);
        assertThat(citizen.getMorale()).isEqualTo(MORALE);
        assertThat(citizen.getSatiety()).isEqualTo(SATIETY);

        Arrays.stream(SkillType.values())
            .forEach(skillType -> verify(skillFactory).create(skillType, CITIZEN_ID));

        verify(skills, times(SkillType.values().length)).add(skill);

        ArgumentCaptor<Durability> durabilityArgumentCaptor = ArgumentCaptor.forClass(Durability.class);
        verify(durabilities).add(durabilityArgumentCaptor.capture());
        Durability durability = durabilityArgumentCaptor.getValue();
        assertThat(durability.getDurabilityId()).isEqualTo(DURABILITY_ID);
        assertThat(durability.getExternalReference()).isEqualTo(CITIZEN_ID);
        assertThat(durability.getMaxHitPoints()).isEqualTo(BASE_HIT_POINTS + HIT_POINTS_PER_LEVEL);
        assertThat(durability.getCurrentHitPoints()).isEqualTo(BASE_HIT_POINTS + HIT_POINTS_PER_LEVEL);
    }
}