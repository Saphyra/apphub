package com.github.saphyra.apphub.service.skyxplore.data.save_game.reserved_storage;


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
public class ReservedStorageRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String RESERVED_STORAGE_ID_1 = "reserved-storage-id-1";
    private static final String RESERVED_STORAGE_ID_2 = "reserved-storage-id-2";

    @Autowired
    private ReservedStorageRepository underTest;

    @After
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
}