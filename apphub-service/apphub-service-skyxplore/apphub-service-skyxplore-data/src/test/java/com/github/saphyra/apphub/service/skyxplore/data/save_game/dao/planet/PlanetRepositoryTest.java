package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class PlanetRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String PLANET_ID_1 = "planet-id-1";
    private static final String PLANET_ID_2 = "planet-id-2";
    private static final String SOLAR_SYSTEM_ID_1 = "solar-system-id-1";
    private static final String SOLAR_SYSTEM_ID_2 = "solar-system-id-2";

    @Autowired
    private PlanetRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        PlanetEntity entity1 = PlanetEntity.builder()
            .planetId(PLANET_ID_1)
            .gameId(GAME_ID_1)
            .build();

        PlanetEntity entity2 = PlanetEntity.builder()
            .planetId(PLANET_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getBySolarSystemId() {
        PlanetEntity entity1 = PlanetEntity.builder()
            .planetId(PLANET_ID_1)
            .gameId(GAME_ID_1)
            .solarSystemId(SOLAR_SYSTEM_ID_1)
            .build();

        PlanetEntity entity2 = PlanetEntity.builder()
            .planetId(PLANET_ID_2)
            .gameId(GAME_ID_2)
            .solarSystemId(SOLAR_SYSTEM_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<PlanetEntity> result = underTest.getBySolarSystemId(SOLAR_SYSTEM_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}