package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class ProductionOrderRepositoryTest {
    private static final String PRODUCTION_ORDER_ID_1 = "production-order-id-1";
    private static final String PRODUCTION_ORDER_ID_2 = "production-order-id-2";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String LOCATION_1 = "location-1";
    private static final String LOCATION_2 = "location-2";

    @Autowired
    private ProductionOrderRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        ProductionOrderEntity entity1 = ProductionOrderEntity.builder()
            .productionOrderId(PRODUCTION_ORDER_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ProductionOrderEntity entity2 = ProductionOrderEntity.builder()
            .productionOrderId(PRODUCTION_ORDER_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByLocation() {
        ProductionOrderEntity entity1 = ProductionOrderEntity.builder()
            .productionOrderId(PRODUCTION_ORDER_ID_1)
            .location(LOCATION_1)
            .build();
        ProductionOrderEntity entity2 = ProductionOrderEntity.builder()
            .productionOrderId(PRODUCTION_ORDER_ID_2)
            .location(LOCATION_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<ProductionOrderEntity> result = underTest.getByLocation(LOCATION_1);

        assertThat(result).containsExactly(entity1);
    }
}