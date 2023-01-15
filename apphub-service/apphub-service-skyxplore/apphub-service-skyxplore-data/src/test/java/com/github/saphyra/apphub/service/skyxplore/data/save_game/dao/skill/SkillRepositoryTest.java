package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class SkillRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SKILL_ID_1 = "skill-id-1";
    private static final String SKILL_ID_2 = "skill-id-2";
    private static final String CITIZEN_ID_1 = "citizen-id-1";
    private static final String CITIZEN_ID_2 = "citizen-id-2";

    @Autowired
    private SkillRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        SkillEntity entity1 = SkillEntity.builder()
            .skillId(SKILL_ID_1)
            .gameId(GAME_ID_1)
            .build();
        SkillEntity entity2 = SkillEntity.builder()
            .skillId(SKILL_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByCitizenId() {
        SkillEntity entity1 = SkillEntity.builder()
            .skillId(SKILL_ID_1)
            .gameId(GAME_ID_1)
            .citizenId(CITIZEN_ID_1)
            .build();
        SkillEntity entity2 = SkillEntity.builder()
            .skillId(SKILL_ID_2)
            .gameId(GAME_ID_2)
            .citizenId(CITIZEN_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<SkillEntity> result = underTest.getByCitizenId(CITIZEN_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}