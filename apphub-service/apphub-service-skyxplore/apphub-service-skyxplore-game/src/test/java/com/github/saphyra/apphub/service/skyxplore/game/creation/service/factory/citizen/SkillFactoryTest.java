package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SkillFactoryTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer INITIAL_NEXT_LEVEL = 36;
    private static final UUID SKILL_ID = UUID.randomUUID();

    @Mock
    private GameCreationProperties gameCreationProperties;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private SkillFactory underTest;

    @Mock
    private GameCreationProperties.SkillProperties skillProperties;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(SKILL_ID);
        given(gameCreationProperties.getSkill()).willReturn(skillProperties);
        given(skillProperties.getInitialNextLevel()).willReturn(INITIAL_NEXT_LEVEL);

        Skill result = underTest.create(SkillType.AIMING, CITIZEN_ID);

        assertThat(result.getSkillId()).isEqualTo(SKILL_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getSkillType()).isEqualTo(SkillType.AIMING);
        assertThat(result.getLevel()).isEqualTo(1);
        assertThat(result.getExperience()).isEqualTo(0);
        assertThat(result.getNextLevel()).isEqualTo(INITIAL_NEXT_LEVEL);
    }
}