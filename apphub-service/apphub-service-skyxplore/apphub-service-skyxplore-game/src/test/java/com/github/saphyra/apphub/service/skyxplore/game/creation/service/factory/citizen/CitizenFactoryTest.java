package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.RandomNameProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CitizenFactoryTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String CITIZEN_NAME = "citizen-name";
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private RandomNameProvider randomNameProvider;

    @Mock
    private SkillFactory skillFactory;

    @InjectMocks
    private CitizenFactory underTest;

    @Mock
    private Skill skill;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(CITIZEN_ID);
        given(randomNameProvider.getRandomName(Collections.emptyList())).willReturn(CITIZEN_NAME);
        given(skillFactory.create(any(), eq(CITIZEN_ID))).willReturn(skill);

        Citizen citizen = underTest.create(PLANET_ID);

        assertThat(citizen.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(citizen.getName()).isEqualTo(CITIZEN_NAME);
        assertThat(citizen.getLocation()).isEqualTo(PLANET_ID);
        assertThat(citizen.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(citizen.getMorale()).isEqualTo(100);
        assertThat(citizen.getSatiety()).isEqualTo(100);

        assertThat(citizen.getSkills()).hasSize(1);

        for (SkillType skillType : SkillType.values()) {
            verify(skillFactory).create(skillType, CITIZEN_ID);
        }
    }
}