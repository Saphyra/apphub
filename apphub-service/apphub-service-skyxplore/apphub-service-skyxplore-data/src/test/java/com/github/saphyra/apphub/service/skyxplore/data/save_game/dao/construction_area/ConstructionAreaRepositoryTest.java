package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction_area;

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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class ConstructionAreaRepositoryTest {
    private static final String CONSTRUCTION_AREA_ID_1 = "construction-area-id-1";
    private static final String CONSTRUCTION_AREA_ID_2 = "construction-area-id-2";
    private static final String CONSTRUCTION_AREA_ID_3 = "construction-area-id-3";
    private static final String CONSTRUCTION_AREA_ID_4 = "construction-area-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";

    @Autowired
    private ConstructionAreaRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }


    @Test
    @Transactional
    void deleteByGameId() {
        ConstructionAreaEntity entity1 = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);

        ConstructionAreaEntity entity2 = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByGameId() {
        ConstructionAreaEntity entity1 = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);

        ConstructionAreaEntity entity2 = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_2)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity2);

        ConstructionAreaEntity entity3 = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_3)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity3);

        ConstructionAreaEntity entity4 = ConstructionAreaEntity.builder()
            .constructionAreaId(CONSTRUCTION_AREA_ID_4)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity4);

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactlyInAnyOrder(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactlyInAnyOrder(entity4);
    }
}