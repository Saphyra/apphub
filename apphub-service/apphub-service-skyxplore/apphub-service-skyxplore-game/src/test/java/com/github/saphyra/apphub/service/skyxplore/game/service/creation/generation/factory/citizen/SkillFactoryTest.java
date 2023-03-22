package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenSkillProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population.SkillFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkillFactoryTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer INITIAL_NEXT_LEVEL = 36;
    private static final UUID SKILL_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private SkillFactory underTest;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenSkillProperties skillProperties;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(SKILL_ID);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getSkill()).willReturn(skillProperties);
        given(gameProperties.getCitizen().getSkill()).willReturn(skillProperties);
        given(skillProperties.getExperiencePerLevel()).willReturn(INITIAL_NEXT_LEVEL);

        Skill result = underTest.create(SkillType.AIMING, CITIZEN_ID);

        assertThat(result.getSkillId()).isEqualTo(SKILL_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getSkillType()).isEqualTo(SkillType.AIMING);
        assertThat(result.getLevel()).isEqualTo(1);
        assertThat(result.getExperience()).isEqualTo(0);
        assertThat(result.getNextLevel()).isEqualTo(INITIAL_NEXT_LEVEL);
    }
}