package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.service.skyxplore.data.common.CoordinateEntity;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.CoordinateTestRepository;
import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class SolarSystemRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SOLAR_SYSTEM_ID_1 = "solar-system-id-1";
    private static final String SOLAR_SYSTEM_ID_2 = "solar-system-id-2";

    @Autowired
    private SolarSystemRepository underTest;

    @Autowired
    private CoordinateTestRepository coordinateRepository;

    @After
    public void clear() {
        underTest.deleteAll();
        coordinateRepository.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        CoordinateEntity coordinate1 = CoordinateEntity.builder()
            .referenceId(SOLAR_SYSTEM_ID_1)
            .x(324d)
            .y(345d)
            .build();
        SolarSystemEntity entity1 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_1)
            .gameId(GAME_ID_1)
            .coordinate(coordinate1)
            .build();

        CoordinateEntity coordinate2 = CoordinateEntity.builder()
            .referenceId(SOLAR_SYSTEM_ID_2)
            .x(23423d)
            .y(23411d)
            .build();
        SolarSystemEntity entity2 = SolarSystemEntity.builder()
            .solarSystemId(SOLAR_SYSTEM_ID_2)
            .gameId(GAME_ID_2)
            .coordinate(coordinate2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
        assertThat(coordinateRepository.findAll()).containsExactly(coordinate2);
    }
}