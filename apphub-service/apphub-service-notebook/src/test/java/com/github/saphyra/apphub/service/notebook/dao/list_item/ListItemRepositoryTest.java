package com.github.saphyra.apphub.service.notebook.dao.list_item;

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
public class ListItemRepositoryTest {
    private static final String LIST_ITEM_ID_1 = "list-item-id-1";
    private static final String LIST_ITEM_ID_2 = "list-item-id-2";
    private static final String LIST_ITEM_ID_3 = "list-item-id-3";
    private static final String USER_ID_1 = "user-id-1";
    private static final String USER_ID_2 = "user-id-2";
    private static final String PARENT_1 = "parent-1";
    private static final String PARENT_2 = "parent-2";

    @Autowired
    private ListItemRepository underTest;

    @After
    public void clear() {
        underTest.deleteAll();
    }

    @Test
    public void getByUserIdAndType() {
        ListItemEntity entity1 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID_1)
            .type(ListItemType.CHECKLIST)
            .build();
        ListItemEntity entity2 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID_2)
            .type(ListItemType.CHECKLIST)
            .build();
        ListItemEntity entity3 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_3)
            .userId(USER_ID_1)
            .type(ListItemType.CATEGORY)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2, entity3));

        List<ListItemEntity> result = underTest.getByUserIdAndType(USER_ID_1, ListItemType.CHECKLIST);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    public void getByParent() {
        ListItemEntity entity1 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_1)
            .parent(PARENT_1)
            .build();
        ListItemEntity entity2 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_3)
            .parent(PARENT_2)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        List<ListItemEntity> result = underTest.getByParent(PARENT_1);

        assertThat(result).containsExactly(entity1);
    }

    @Test
    @Transactional
    public void deleteByUserId() {
        ListItemEntity entity1 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_1)
            .userId(USER_ID_1)
            .parent(PARENT_1)
            .build();
        ListItemEntity entity2 = ListItemEntity.builder()
            .listItemId(LIST_ITEM_ID_2)
            .userId(USER_ID_2)
            .parent(PARENT_1)
            .build();
        underTest.saveAll(Arrays.asList(entity1, entity2));

        underTest.deleteByUserId(USER_ID_2);

        assertThat(underTest.findAll()).containsExactly(entity1);
    }
}