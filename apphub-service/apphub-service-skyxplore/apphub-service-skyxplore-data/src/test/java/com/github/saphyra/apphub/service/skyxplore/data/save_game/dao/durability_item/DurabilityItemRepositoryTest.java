package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

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
public class DurabilityItemRepositoryTest {
    private static final String GAME_ID_1 = "game-id-1";
    private static final String GAME_ID_2 = "game-id-2";
    private static final String DURABILITY_ITEM_ID_1 = "durability-item-id-1";
    private static final String DURABILITY_ITEM_ID_2 = "durability-item-id-2";
    private static final String PARENT_1 = "parent-1";
    private static final String PARENT_2 = "parent-2";

    @Autowired
    private DurabilityItemRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    @Transactional
    public void deleteByGameId() {
        DurabilityItemEntity entity1 = DurabilityItemEntity.builder()
            .durabilityItemId(DURABILITY_ITEM_ID_1)
            .gameId(GAME_ID_1)
            .build();
        DurabilityItemEntity entity2 = DurabilityItemEntity.builder()
            .durabilityItemId(DURABILITY_ITEM_ID_2)
            .gameId(GAME_ID_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByGameId(GAME_ID_1);

        assertThat(underTest.findAll()).containsExactly(entity2);
    }

    @Test
    public void getByParent() {
        DurabilityItemEntity entity1 = DurabilityItemEntity.builder()
            .durabilityItemId(DURABILITY_ITEM_ID_1)
            .parent(PARENT_1)
            .build();
        DurabilityItemEntity entity2 = DurabilityItemEntity.builder()
            .durabilityItemId(DURABILITY_ITEM_ID_2)
            .parent(PARENT_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<DurabilityItemEntity> result = underTest.getByParent(PARENT_1);

        assertThat(result).containsExactly(entity1);
    }
}