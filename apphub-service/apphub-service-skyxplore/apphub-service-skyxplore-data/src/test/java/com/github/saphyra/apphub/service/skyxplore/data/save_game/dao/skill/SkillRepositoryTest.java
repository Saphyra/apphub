package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.skill;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class SkillRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SKILL_ID_1 = "skill-id-1";
    private static final String SKILL_ID_2 = "skill-id-2";
    private static final String SKILL_ID_3 = "skill-id-3";
    private static final String SKILL_ID_4 = "skill-id-4";
    private static final String CITIZEN_ID_1 = "citizen-id-1";
    private static final String CITIZEN_ID_2 = "citizen-id-2";

    @Autowired
    private SkillRepository underTest;

    @AfterEach
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

    @Test
    void getByGameId() {
        SkillEntity entity1 = SkillEntity.builder()
            .skillId(SKILL_ID_1)
            .gameId(GAME_ID_1)
            .build();
        SkillEntity entity2 = SkillEntity.builder()
            .skillId(SKILL_ID_2)
            .gameId(GAME_ID_1)
            .build();
        SkillEntity entity3 = SkillEntity.builder()
            .skillId(SKILL_ID_3)
            .gameId(GAME_ID_1)
            .build();
        SkillEntity entity4 = SkillEntity.builder()
            .skillId(SKILL_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}