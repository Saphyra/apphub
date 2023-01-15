package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

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
public class SurfaceRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SURFACE_ID_1 = "surface-id-1";
    private static final String SURFACE_ID_2 = "surface-id-2";
    private static final String PLANET_ID_1 = "planet-id-1";
    private static final String PLANET_ID_2 = "planet-id-2";

    @Autowired
    private SurfaceRepository underTest;

    @After
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
}