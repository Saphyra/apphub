package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

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
class CitizenAllocationRepositoryTest {
    private static final String CITIZEN_ALLOCATION_ID_1 = "citizen-allocation-id-1";
    private static final String CITIZEN_ALLOCATION_ID_2 = "citizen-allocation-id-2";
    private static final String CITIZEN_ALLOCATION_ID_3 = "citizen-allocation-id-3";
    private static final String CITIZEN_ALLOCATION_ID_4 = "citizen-allocation-id-4";
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";

    @Autowired
    private CitizenAllocationRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    void deleteByGameId() {
        CitizenAllocationEntity entity1 = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        CitizenAllocationEntity entity2 = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(List.of(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByGameId() {
        CitizenAllocationEntity entity1 = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_1)
            .gameId(GAME_ID_1)
            .build();
        CitizenAllocationEntity entity2 = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_2)
            .gameId(GAME_ID_1)
            .build();
        CitizenAllocationEntity entity3 = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_3)
            .gameId(GAME_ID_1)
            .build();
        CitizenAllocationEntity entity4 = CitizenAllocationEntity.builder()
            .citizenAllocationId(CITIZEN_ALLOCATION_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}