package com.github.saphyra.apphub.service.skyxplore.game.common.converter.response;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkillToResponseConverterTest {
    private static final Integer EXPERIENCE = 1243;
    private static final Integer LEVEL = 41;
    private static final Integer NEXT_LEVEL = 413;

    @InjectMocks
    private SkillToResponseConverter underTest;

    @Mock
    private Skill skill;

    @Test
    public void getSkills() {
        given(skill.getExperience()).willReturn(EXPERIENCE);
        given(skill.getLevel()).willReturn(LEVEL);
        given(skill.getNextLevel()).willReturn(NEXT_LEVEL);

        Map<String, SkillResponse> result = underTest.getSkills(CollectionUtils.singleValueMap(SkillType.AIMING, skill));

        assertThat(result).containsEntry(SkillType.AIMING.name(), SkillResponse.builder().experience(EXPERIENCE).nextLevel(NEXT_LEVEL).level(LEVEL).build());
    }
}