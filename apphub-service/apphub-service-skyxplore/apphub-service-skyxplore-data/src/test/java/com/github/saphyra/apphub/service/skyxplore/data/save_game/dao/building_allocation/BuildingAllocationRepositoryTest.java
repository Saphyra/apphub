package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

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
class BuildingAllocationRepositoryTest {
    private static final String BUILDING_ALLOCATION_ID_1 = "building-allocation-id-1";
    private static final String BUILDING_ALLOCATION_ID_2 = "building-allocation-id-2";
    private static final String BUILDING_ALLOCATION_ID_3 = "building-allocation-id-3";
    private static final String BUILDING_ALLOCATION_ID_4 = "building-allocation-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";

    @Autowired
    private BuildingAllocationRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByGameId() {
        BuildingModuleAllocationEntity entity1 = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        BuildingModuleAllocationEntity entity2 = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByGameId() {
        BuildingModuleAllocationEntity entity1 = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        BuildingModuleAllocationEntity entity2 = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_2)
            .gameId(GAME_ID_1)
            .build();
        BuildingModuleAllocationEntity entity3 = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_3)
            .gameId(GAME_ID_1)
            .build();
        BuildingModuleAllocationEntity entity4 = BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(BUILDING_ALLOCATION_ID_4)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}