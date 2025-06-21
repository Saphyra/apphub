package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

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
class ProductionOrderRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String ID_1 = "id-1";
    private static final String ID_2 = "id-2";
    private static final String ID_3 = "id-3";
    private static final String ID_4 = "id-4";

    @Autowired
    private ProductionOrderRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByGameId() {
        ProductionOrderEntity entity1 = ProductionOrderEntity.builder()
            .productionOrderId(ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);
        ProductionOrderEntity entity2 = ProductionOrderEntity.builder()
            .productionOrderId(ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByGameId() {
        ProductionOrderEntity entity1 = ProductionOrderEntity.builder()
            .productionOrderId(ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);
        ProductionOrderEntity entity2 = ProductionOrderEntity.builder()
            .productionOrderId(ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity2);
        ProductionOrderEntity entity3 = ProductionOrderEntity.builder()
            .productionOrderId(ID_3)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity3);
        ProductionOrderEntity entity4 = ProductionOrderEntity.builder()
            .productionOrderId(ID_4)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity4);

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactlyInAnyOrder(entity1, entity3);
    }
}