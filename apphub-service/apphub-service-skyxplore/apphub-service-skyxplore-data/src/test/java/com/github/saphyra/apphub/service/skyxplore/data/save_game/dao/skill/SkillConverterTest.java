package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SkillConverterTest {
    private static final UUID SKILL_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String SKILL_TYPE = "skill-type";
    private static final Integer LEVEL = 32456;
    private static final Integer EXPERIENCE = 657345;
    private static final Integer NEXT_LEVEL = 45637645;
    private static final String SKILL_ID_STRING = "skill-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String CITIZEN_ID_STRING = "citizen-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private SkillConverter underTest;

    @Test
    public void convertDomain() {
        SkillModel model = new SkillModel();
        model.setId(SKILL_ID);
        model.setGameId(GAME_ID);
        model.setCitizenId(CITIZEN_ID);
        model.setSkillType(SKILL_TYPE);
        model.setLevel(LEVEL);
        model.setExperience(EXPERIENCE);
        model.setNextLevel(NEXT_LEVEL);

        given(uuidConverter.convertDomain(SKILL_ID)).willReturn(SKILL_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);

        SkillEntity result = underTest.convertDomain(model);


        assertThat(result.getSkillId()).isEqualTo(SKILL_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID_STRING);
        assertThat(result.getSkillType()).isEqualTo(SKILL_TYPE);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getExperience()).isEqualTo(EXPERIENCE);
        assertThat(result.getNextLevel()).isEqualTo(NEXT_LEVEL);
    }

    @Test
    public void convertEntity() {
        SkillEntity entity = SkillEntity.builder()
            .skillId(SKILL_ID_STRING)
            .gameId(GAME_ID_STRING)
            .citizenId(CITIZEN_ID_STRING)
            .skillType(SKILL_TYPE)
            .level(LEVEL)
            .experience(EXPERIENCE)
            .nextLevel(NEXT_LEVEL)
            .build();

        given(uuidConverter.convertEntity(SKILL_ID_STRING)).willReturn(SKILL_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(CITIZEN_ID_STRING)).willReturn(CITIZEN_ID);

        SkillModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(SKILL_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.SKILL);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getSkillType()).isEqualTo(SKILL_TYPE);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getExperience()).isEqualTo(EXPERIENCE);
        assertThat(result.getNextLevel()).isEqualTo(NEXT_LEVEL);
    }
}