package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.process;

import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
public class ProcessRepositoryTest {
    private static final String PROCESS_ID_1 = "process-id-1";
    private static final String PROCESS_ID_2 = "process-id-2";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";

    @Autowired
    private ProcessRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        ProcessEntity entity1 = ProcessEntity.builder()
            .processId(PROCESS_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ProcessEntity entity2 = ProcessEntity.builder()
            .processId(PROCESS_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByGameId() {
        ProcessEntity entity1 = ProcessEntity.builder()
            .processId(PROCESS_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ProcessEntity entity2 = ProcessEntity.builder()
            .processId(PROCESS_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        List<ProcessEntity> result = underTest.getByGameId(GAME_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}