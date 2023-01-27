package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

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
public class CoordinateRepositoryTest {
    private static final String COORDINATE_ID_1 = "coordinate-id-1";
    private static final String COORDINATE_ID_2 = "coordinate-id-2";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String REFERENCE_ID_1 = "reference-id-1";
    private static final String REFERENCE_ID_2 = "reference-id-2";

    @Autowired
    private CoordinateRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        CoordinateEntity entity1 = CoordinateEntity.builder()
            .coordinateId(COORDINATE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        CoordinateEntity entity2 = CoordinateEntity.builder()
            .coordinateId(COORDINATE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByReferenceId() {
        CoordinateEntity entity1 = CoordinateEntity.builder()
            .coordinateId(COORDINATE_ID_1)
            .gameId(GAME_ID_1)
            .referenceId(REFERENCE_ID_1)
            .build();
        CoordinateEntity entity2 = CoordinateEntity.builder()
            .coordinateId(COORDINATE_ID_2)
            .referenceId(REFERENCE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<CoordinateEntity> result = underTest.getByReferenceId(REFERENCE_ID_1);

        assertThat(result).containsExactly(entity1);
    }
}