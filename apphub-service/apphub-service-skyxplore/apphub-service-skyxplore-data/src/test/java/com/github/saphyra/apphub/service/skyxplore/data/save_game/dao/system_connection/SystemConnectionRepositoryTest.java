package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

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
import java.util.List;

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

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        SystemConnectionEntity entity1 = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        SystemConnectionEntity entity2 = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByGameId() {
        SystemConnectionEntity entity1 = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        SystemConnectionEntity entity2 = SystemConnectionEntity.builder()
            .systemConnectionId(SYSTEM_CONNECTION_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<SystemConnectionEntity> result = underTest.getByGameId(GAME_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}