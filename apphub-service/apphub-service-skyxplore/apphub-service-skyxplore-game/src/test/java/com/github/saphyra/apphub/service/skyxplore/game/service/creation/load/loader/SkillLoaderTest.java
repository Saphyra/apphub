package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkillLoaderTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID SKILL_ID = UUID.randomUUID();
    private static final Integer LEVEL = 24;
    private static final Integer EXPERIENCE = 53;
    private static final Integer NEXT_LEVEL = 6453;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private SkillLoader underTest;

    @Mock
    private SkillModel skillModel;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(CITIZEN_ID, GameItemType.SKILL, SkillModel[].class)).willReturn(Arrays.asList(skillModel));

        given(skillModel.getId()).willReturn(SKILL_ID);
        given(skillModel.getCitizenId()).willReturn(CITIZEN_ID);
        given(skillModel.getSkillType()).willReturn(SkillType.AIMING.name());
        given(skillModel.getLevel()).willReturn(LEVEL);
        given(skillModel.getExperience()).willReturn(EXPERIENCE);
        given(skillModel.getNextLevel()).willReturn(NEXT_LEVEL);

        Map<SkillType, Skill> result = underTest.load(CITIZEN_ID);

        assertThat(result).hasSize(1);
        Skill skill = result.get(SkillType.AIMING);
        assertThat(skill.getSkillId()).isEqualTo(SKILL_ID);
        assertThat(skill.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(skill.getSkillType()).isEqualTo(SkillType.AIMING);
        assertThat(skill.getLevel()).isEqualTo(LEVEL);
        assertThat(skill.getNextLevel()).isEqualTo(NEXT_LEVEL);
        assertThat(skill.getExperience()).isEqualTo(EXPERIENCE);
    }
}