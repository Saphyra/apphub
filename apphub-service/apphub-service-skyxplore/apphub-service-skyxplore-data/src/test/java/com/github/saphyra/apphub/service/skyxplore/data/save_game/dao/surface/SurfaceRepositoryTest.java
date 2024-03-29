package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

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
public class SurfaceRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SURFACE_ID_1 = "surface-id-1";
    private static final String SURFACE_ID_2 = "surface-id-2";
    private static final String SURFACE_ID_3 = "surface-id-3";
    private static final String SURFACE_ID_4 = "surface-id-4";
    private static final String PLANET_ID_1 = "planet-id-1";
    private static final String PLANET_ID_2 = "planet-id-2";

    @Autowired
    private SurfaceRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        SurfaceEntity entity1 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        SurfaceEntity entity2 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByPlanetId() {
        SurfaceEntity entity1 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_1)
            .gameId(GAME_ID_1)
            .planetId(PLANET_ID_1)
            .build();
        SurfaceEntity entity2 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_2)
            .gameId(GAME_ID_2)
            .planetId(PLANET_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<SurfaceEntity> result = underTest.getByPlanetId(PLANET_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    void getByGameId() {
        SurfaceEntity entity1 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        SurfaceEntity entity2 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_2)
            .gameId(GAME_ID_1)
            .build();
        SurfaceEntity entity3 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_3)
            .gameId(GAME_ID_1)
            .build();
        SurfaceEntity entity4 = SurfaceEntity.builder()
            .surfaceId(SURFACE_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}