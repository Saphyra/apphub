package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
@Slf4j
class DeconstructionRepositoryTest {
    private static final String DECONSTRUCTION_ID_1 = "deconstruction-id-1";
    private static final String DECONSTRUCTION_ID_2 = "deconstruction-id-2";
    private static final String DECONSTRUCTION_ID_3 = "deconstruction-id-3";
    private static final String DECONSTRUCTION_ID_4 = "deconstruction-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String EXTERNAL_REFERENCE_1 = "external-reference-1";
    private static final String EXTERNAL_REFERENCE_2 = "external-reference-2";

    @Autowired
    private DeconstructionRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByGameId() {
        DeconstructionEntity entity1 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        underTest.save(entity1);
        DeconstructionEntity entity2 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.save(entity2);

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByExternalReference() {
        DeconstructionEntity entity1 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_1)
            .externalReference(EXTERNAL_REFERENCE_1)
            .build();
        underTest.save(entity1);
        DeconstructionEntity entity2 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_2)
            .externalReference(EXTERNAL_REFERENCE_2)
            .build();
        underTest.save(entity2);

        List<DeconstructionEntity> result = underTest.getByExternalReference(EXTERNAL_REFERENCE_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    void getByGameId() {
        DeconstructionEntity entity1 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        DeconstructionEntity entity2 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_2)
            .gameId(GAME_ID_1)
            .build();
        DeconstructionEntity entity3 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_3)
            .gameId(GAME_ID_1)
            .build();
        DeconstructionEntity entity4 = DeconstructionEntity.builder()
            .deconstructionId(DECONSTRUCTION_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}