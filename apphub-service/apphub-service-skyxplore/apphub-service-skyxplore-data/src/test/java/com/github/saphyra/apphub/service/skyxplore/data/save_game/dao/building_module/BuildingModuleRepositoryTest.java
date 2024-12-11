package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

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
class BuildingModuleRepositoryTest {
    private static final String BUILDING_MODULE_ID_1 = "building-module-id-1";
    private static final String BUILDING_MODULE_ID_2 = "building-module-id-2";
    private static final String BUILDING_MODULE_ID_3 = "building-module-id-3";
    private static final String BUILDING_MODULE_ID_4 = "building-module-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";

    @Autowired
    private BuildingModuleRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByGameId() {
        BuildingModuleEntity entity1 = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);

        BuildingModuleEntity entity2 = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByGameId() {
        BuildingModuleEntity entity1 = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);

        BuildingModuleEntity entity2 = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_2)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity2);

        BuildingModuleEntity entity3 = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_3)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity3);

        BuildingModuleEntity entity4 = BuildingModuleEntity.builder()
            .buildingModuleId(BUILDING_MODULE_ID_4)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity4);

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactlyInAnyOrder(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactlyInAnyOrder(entity4);
    }
}