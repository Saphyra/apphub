package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class ConstructionRepositoryTest {
    private static final String CONSTRUCTION_ID_1 = "construction-id-1";
    private static final String CONSTRUCTION_ID_2 = "construction-id-2";
    private static final String CONSTRUCTION_ID_3 = "construction-id-3";
    private static final String CONSTRUCTION_ID_4 = "construction-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";

    @Autowired
    private ConstructionRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        ConstructionEntity entity1 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ConstructionEntity entity2 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByLocation() {
        ConstructionEntity entity1 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_1)
            .externalReference(EXTERNAL_REFERENCE_1)
            .build();
        ConstructionEntity entity2 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_2)
            .externalReference(EXTERNAL_REFERENCE_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<ConstructionEntity> result = underTest.getByExternalReference(EXTERNAL_REFERENCE_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    void getByGameId() {
        ConstructionEntity entity1 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ConstructionEntity entity2 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_2)
            .gameId(GAME_ID_1)
            .build();
        ConstructionEntity entity3 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_3)
            .gameId(GAME_ID_1)
            .build();
        ConstructionEntity entity4 = ConstructionEntity.builder()
            .constructionId(CONSTRUCTION_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}