package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SkillToModelConverterTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID SKILL_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final int LEVEL = 245;
    private static final int EXPERIENCE = 47;
    private static final int NEXT_LEVEL = 56;

    @InjectMocks
    private SkillToModelConverter underTest;

    @Test
    public void convert() {
        Skill skill = Skill.builder()
            .skillId(SKILL_ID)
            .citizenId(CITIZEN_ID)
            .skillType(SkillType.AIMING)
            .level(LEVEL)
            .experience(EXPERIENCE)
            .nextLevel(NEXT_LEVEL)
            .build();

        List<SkillModel> result = underTest.convert(Arrays.asList(skill), GAME_ID);

        assertThat(result.get(0).getId()).isEqualTo(SKILL_ID);
        assertThat(result.get(0).getGameId()).isEqualTo(GAME_ID);
        assertThat(result.get(0).getType()).isEqualTo(GameItemType.SKILL);
        assertThat(result.get(0).getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.get(0).getSkillType()).isEqualTo(SkillType.AIMING.name());
        assertThat(result.get(0).getLevel()).isEqualTo(LEVEL);
        assertThat(result.get(0).getExperience()).isEqualTo(EXPERIENCE);
        assertThat(result.get(0).getNextLevel()).isEqualTo(NEXT_LEVEL);
    }
}