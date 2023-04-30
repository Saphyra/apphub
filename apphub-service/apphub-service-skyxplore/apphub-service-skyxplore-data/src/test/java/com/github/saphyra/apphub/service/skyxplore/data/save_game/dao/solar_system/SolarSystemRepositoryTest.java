package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.solar_system;

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

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class SolarSystemRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SOLAR_SYSTEM_ID_1 = "solar-system-id-1";
    private static final String SOLAR_SYSTEM_ID_2 = "solar-system-id-2";
    private static final String SOLAR_SYSTEM_ID_3 = "solar-system-id-3";
    private static final String SOLAR_SYSTEM_ID_4 = "solar-system-id-4";

    @Autowired
    private SolarSystemRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        SolarSystemEntity entity1 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_1)
            .gameId(GAME_ID_1)
            .build();

        SolarSystemEntity entity2 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByGameId() {
        SolarSystemEntity entity1 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_1)
            .gameId(GAME_ID_1)
            .build();

        SolarSystemEntity entity2 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_2)
            .gameId(GAME_ID_1)
            .build();

        SolarSystemEntity entity3 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_3)
            .gameId(GAME_ID_1)
            .build();

        SolarSystemEntity entity4 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_4)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}