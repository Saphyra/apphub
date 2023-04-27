package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

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
public class LineRepositoryTest {
    private static final String LINE_ID_1 = "line-id-1";
    private static final String LINE_ID_2 = "line-id-2";
    private static final String LINE_ID_3 = "line-id-3";
    private static final String LINE_ID_4 = "line-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String REFERENCE_ID_1 = "reference-id-1";
    private static final String REFERENCE_ID_2 = "reference-id-2";

    @Autowired
    private LineRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        LineEntity entity1 = LineEntity.builder()
            .lineId(LINE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        LineEntity entity2 = LineEntity.builder()
            .lineId(LINE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByReferenceId() {
        LineEntity entity1 = LineEntity.builder()
            .lineId(LINE_ID_1)
            .gameId(GAME_ID_1)
            .referenceId(REFERENCE_ID_1)
            .build();
        LineEntity entity2 = LineEntity.builder()
            .lineId(LINE_ID_2)
            .gameId(GAME_ID_2)
            .referenceId(REFERENCE_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<LineEntity> result = underTest.getByReferenceId(REFERENCE_ID_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    void getByGameId() {
        LineEntity entity1 = LineEntity.builder()
            .lineId(LINE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        LineEntity entity2 = LineEntity.builder()
            .lineId(LINE_ID_2)
            .gameId(GAME_ID_1)
            .build();
        LineEntity entity3 = LineEntity.builder()
            .lineId(LINE_ID_3)
            .gameId(GAME_ID_1)
            .build();
        LineEntity entity4 = LineEntity.builder()
            .lineId(LINE_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}