package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class BuildingRepositoryTest {
    private static final String BUILDING_ID_1 = "building-id-1";
    private static final String BUILDING_ID_2 = "building-id-2";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SURFACE_ID_1 = "surface-id-1";
    private static final String SURFACE_ID_2 = "surface-id-2";

    @Autowired
    private BuildingRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        BuildingEntity entity1 = BuildingEntity.builder()
            .buildingId(BUILDING_ID_1)
            .gameId(GAME_ID_1)
            .build();

        BuildingEntity entity2 = BuildingEntity.builder()
            .buildingId(BUILDING_ID_2)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void findBySurfaceId() {
        BuildingEntity entity1 = BuildingEntity.builder()
            .buildingId(BUILDING_ID_1)
            .gameId(GAME_ID_1)
            .surfaceId(SURFACE_ID_1)
            .build();

        BuildingEntity entity2 = BuildingEntity.builder()
            .buildingId(BUILDING_ID_2)
            .gameId(GAME_ID_2)
            .surfaceId(SURFACE_ID_2)
            .build();

        underTest.saveAll(Arrays.asList(entity1, entity2));

        Optional<BuildingEntity> result = underTest.findBySurfaceId(SURFACE_ID_1);

        assertThat(result).contains(entity1);
    }
}