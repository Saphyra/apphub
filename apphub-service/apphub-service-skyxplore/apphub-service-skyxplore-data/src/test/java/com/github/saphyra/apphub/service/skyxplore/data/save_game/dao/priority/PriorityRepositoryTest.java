package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.priority;

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
public class PriorityRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String PRIORITY_TYPE_1 = "priority-type-1";
    private static final String PRIORITY_TYPE_2 = "priority-type-2";
    private static final String LOCATION_1 = "location-1";
    private static final String LOCATION_2 = "location-2";

    @Autowired
    private PriorityRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        PriorityEntity entity1 = PriorityEntity.builder()
            .pk(PriorityPk.builder().priorityType(PRIORITY_TYPE_1).location(LOCATION_1).build())
            .gameId(GAME_ID_1)
            .build();
        PriorityEntity entity2 = PriorityEntity.builder()
            .pk(PriorityPk.builder().priorityType(PRIORITY_TYPE_2).location(LOCATION_1).build())
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByLocation() {
        PriorityEntity entity1 = PriorityEntity.builder()
            .pk(PriorityPk.builder().priorityType(PRIORITY_TYPE_1).location(LOCATION_1).build())
            .gameId(GAME_ID_1)
            .build();
        PriorityEntity entity2 = PriorityEntity.builder()
            .pk(PriorityPk.builder().priorityType(PRIORITY_TYPE_2).location(LOCATION_2).build())
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<PriorityEntity> result = underTest.getByPkLocation(LOCATION_1);

        assertThat(result).containsExactly(entity1);
    }
}