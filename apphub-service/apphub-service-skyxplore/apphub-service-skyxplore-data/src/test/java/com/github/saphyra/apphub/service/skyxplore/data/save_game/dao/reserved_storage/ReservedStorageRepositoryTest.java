package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;


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
public class ReservedStorageRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String RESERVED_STORAGE_ID_1 = "reserved-storage-id-1";
    private static final String RESERVED_STORAGE_ID_2 = "reserved-storage-id-2";
    private static final String RESERVED_STORAGE_ID_3 = "reserved-storage-id-3";
    private static final String RESERVED_STORAGE_ID_4 = "reserved-storage-id-4";

    @Autowired
    private ReservedStorageRepository underTest;

    @AfterEach
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        ReservedStorageEntity entity1 = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ReservedStorageEntity entity2 = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    void getByGameId() {
        ReservedStorageEntity entity1 = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        ReservedStorageEntity entity2 = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_2)
            .gameId(GAME_ID_1)
            .build();
        ReservedStorageEntity entity3 = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_3)
            .gameId(GAME_ID_1)
            .build();
        ReservedStorageEntity entity4 = ReservedStorageEntity.builder()
            .reservedStorageId(RESERVED_STORAGE_ID_4)
            .gameId(GAME_ID_2)
            .build();

        underTest.saveAll(List.of(entity1, entity2, entity3, entity4));

        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(0, 2))).containsExactly(entity1, entity2);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(1, 2))).containsExactly(entity3);
        assertThat(underTest.getByGameId(GAME_ID_1, PageRequest.of(2, 2))).isEmpty();
    }
}