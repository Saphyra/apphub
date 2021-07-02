package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.service.skyxplore.data.common.CoordinateEntity;
import com.github.saphyra.apphub.service.skyxplore.data.common.LineEntity;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.CoordinateTestRepository;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.LineTestRepository;
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
public class SystemConnectionRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String SYSTEM_CONNECTION_ID_1 = "system-connection-id-1";
    private static final String SYSTEM_CONNECTION_ID_2 = "system-connection-id-2";

    @Autowired
    private SystemConnectionRepository underTest;

    @Autowired
    private CoordinateTestRepository coordinateRepository;

    @Autowired
    private LineTestRepository lineRepository;

    @After
    public void clear() {
        underTest.deleteAll();
        coordinateRepository.deleteAll();
        lineRepository.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        CoordinateEntity coordinate1a = CoordinateEntity.builder()
            .referenceId(SYSTEM_CONNECTION_ID_1 + "a")
            .x(3242d)
            .y(3225d)
            .build();
        CoordinateEntity coordinate1b = CoordinateEntity.builder()
            .referenceId(SYSTEM_CONNECTION_ID_1 + "b")
            .x(324252d)
            .y(3254325d)
            .build();
        CoordinateEntity coordinate2a = CoordinateEntity.builder()
            .referenceId(SYSTEM_CONNECTION_ID_2 + "a")
            .x(3123242d)
            .y(334225d)
            .build();
        CoordinateEntity coordinate2b = CoordinateEntity.builder()
            .referenceId(SYSTEM_CONNECTION_ID_2 + "b")
            .x(3236742d)
            .y(376225d)
            .build();
        LineEntity line1 = LineEntity.builder()
            .referenceId(SYSTEM_CONNECTION_ID_1)
            .aId(SYSTEM_CONNECTION_ID_1 + "a")
            .bId(SYSTEM_CONNECTION_ID_1 + "b")
            .a(coordinate1a)
            .b(coordinate1b)
            .build();
        LineEntity line2 = LineEntity.builder()
            .referenceId(SYSTEM_CONNECTION_ID_2)
            .aId(SYSTEM_CONNECTION_ID_2 + "a")
            .bId(SYSTEM_CONNECTION_ID_2 + "b")
            .a(coordinate2a)
            .b(coordinate2b)
            .build();
        SystemConnectionEntity entity1 = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_1)
            .gameId(GAME_ID_1)
            .line(line1)
            .build();
        SystemConnectionEntity entity2 = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_2)
            .gameId(GAME_ID_2)
            .line(line2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
        assertThat(lineRepository.findAll()).containsExactly(line2);
        assertThat(coordinateRepository.findAll()).containsExactlyInAnyOrder(coordinate2a, coordinate2b);
    }
}