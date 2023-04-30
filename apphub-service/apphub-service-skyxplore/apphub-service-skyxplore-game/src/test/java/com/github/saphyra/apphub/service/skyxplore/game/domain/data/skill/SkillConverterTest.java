package com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkillConverterTest {
    private static final UUID SKILL_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer LEVEL = 2345;
    private static final Integer EXPERIENCE = 3645;
    private static final Integer NEXT_LEVEL = 234;

    private final SkillConverter underTest = new SkillConverter();

    @Mock
    private GameData gameData;

    @Mock
    private Skills skills;

    @Test
    void toModel() {
        Skill skill = Skill.builder()
            .skillId(SKILL_ID)
            .citizenId(CITIZEN_ID)
            .skillType(SkillType.BUILDING)
            .level(LEVEL)
            .experience(EXPERIENCE)
            .nextLevel(NEXT_LEVEL)
            .build();

        SkillModel result = underTest.toModel(GAME_ID, skill);

        assertThat(result.getId()).isEqualTo(SKILL_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SKILL);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getSkillType()).isEqualTo(SkillType.BUILDING.name());
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getExperience()).isEqualTo(EXPERIENCE);
        assertThat(result.getNextLevel()).isEqualTo(NEXT_LEVEL);
    }

    @Test
    void toResponse() {
        Skill skill = Skill.builder()
            .skillId(SKILL_ID)
            .citizenId(CITIZEN_ID)
            .skillType(SkillType.BUILDING)
            .level(LEVEL)
            .experience(EXPERIENCE)
            .nextLevel(NEXT_LEVEL)
            .build();

        given(gameData.getSkills()).willReturn(skills);
        given(skills.getByCitizenId(CITIZEN_ID)).willReturn(List.of(skill));

        Map<String, SkillResponse> result = underTest.toResponse(gameData, CITIZEN_ID);

        assertThat(result.get(SkillType.BUILDING.name()).getExperience()).isEqualTo(EXPERIENCE);
        assertThat(result.get(SkillType.BUILDING.name()).getLevel()).isEqualTo(LEVEL);
        assertThat(result.get(SkillType.BUILDING.name()).getNextLevel()).isEqualTo(NEXT_LEVEL);
    }
}