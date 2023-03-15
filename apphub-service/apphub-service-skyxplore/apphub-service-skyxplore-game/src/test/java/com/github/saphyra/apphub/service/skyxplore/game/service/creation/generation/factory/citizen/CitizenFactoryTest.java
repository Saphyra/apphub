package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSatietyProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapon;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.RandomNameProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CitizenFactoryTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String CITIZEN_NAME = "citizen-name";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final int DEFAULT_MORALE = 1234;
    private static final int DEFAULT_SATIETY = 234;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RandomNameProvider randomNameProvider;

    @Mock
    private SkillFactory skillFactory;

    @Mock
    private GameProperties properties;

    @Mock
    private SoldierDataFactory soldierDataFactory;

    @InjectMocks
    private CitizenFactory underTest;

    @Mock
    private Skill skill;

    @Mock
    private SoldierWeapon soldierData;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties moraleProperties;

    @Mock
    private CitizenSatietyProperties satietyProperties;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(CITIZEN_ID);
        given(randomNameProvider.getRandomName(Collections.emptyList())).willReturn(CITIZEN_NAME);
        given(skillFactory.create(any(), eq(CITIZEN_ID))).willReturn(skill);
        given(properties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(citizenProperties.getSatiety()).willReturn(satietyProperties);
        given(moraleProperties.getMax()).willReturn(DEFAULT_MORALE);
        given(satietyProperties.getMax()).willReturn(DEFAULT_SATIETY);
        given(soldierDataFactory.create()).willReturn(soldierData);

        Citizen citizen = underTest.create(PLANET_ID);

        assertThat(citizen.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(citizen.getName()).isEqualTo(CITIZEN_NAME);
        assertThat(citizen.getLocation()).isEqualTo(PLANET_ID);
        assertThat(citizen.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(citizen.getMorale()).isEqualTo(DEFAULT_MORALE);
        assertThat(citizen.getSatiety()).isEqualTo(DEFAULT_SATIETY);
        assertThat(citizen.getSoldierData()).isEqualTo(soldierData);

        assertThat(citizen.getSkills()).hasSize(1);

        for (SkillType skillType : SkillType.values()) {
            verify(skillFactory).create(skillType, CITIZEN_ID);
        }
    }
}