package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;


import com.github.saphyra.apphub.test.common.repository.RepositoryTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class StoredResourceRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String STORED_RESOURCE_ID_1 = "stored-resource-id-1";
    private static final String STORED_RESOURCE_ID_2 = "stored-resource-id-2";
    private static final String LOCATION_1 = "location-1";
    private static final String LOCATION_2 = "location-2";

    @Autowired
    private StoredResourceRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        StoredResourceEntity entity1 = StoredResourceEntity.builder()
            .storedResourceId(STORED_RESOURCE_ID_1)
            .gameId(GAME_ID_1)
            .build();
        StoredResourceEntity entity2 = StoredResourceEntity.builder()
            .storedResourceId(STORED_RESOURCE_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByLocation() {
        StoredResourceEntity entity1 = StoredResourceEntity.builder()
            .storedResourceId(STORED_RESOURCE_ID_1)
            .gameId(GAME_ID_1)
            .location(LOCATION_1)
            .build();
        StoredResourceEntity entity2 = StoredResourceEntity.builder()
            .storedResourceId(STORED_RESOURCE_ID_2)
            .gameId(GAME_ID_2)
            .location(LOCATION_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<StoredResourceEntity> result = underTest.getByLocation(LOCATION_1);

        assertThat(result).containsExactly(entity1);
    }
}