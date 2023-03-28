package com.github.saphyra.apphub.service.skyxplore.game.common.converter.response;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SkillToResponseConverterTest {
    private static final Integer EXPERIENCE = 1243;
    private static final Integer LEVEL = 41;
    private static final Integer NEXT_LEVEL = 413;
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @InjectMocks
    private SkillToResponseConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Skills skills;

    @Mock
    private Skill skill;

    @Test
    public void getSkills() {
        given(gameData.getSkills()).willReturn(skills);
        given(skills.getByCitizenId(CITIZEN_ID)).willReturn(List.of(skill));

        given(skill.getExperience()).willReturn(EXPERIENCE);
        given(skill.getLevel()).willReturn(LEVEL);
        given(skill.getNextLevel()).willReturn(NEXT_LEVEL);
        given(skill.getSkillType()).willReturn(SkillType.AIMING);

        Map<String, SkillResponse> result = underTest.getSkills(gameData, CITIZEN_ID);

        assertThat(result).containsEntry(SkillType.AIMING.name(), SkillResponse.builder().experience(EXPERIENCE).nextLevel(NEXT_LEVEL).level(LEVEL).build());
    }
}