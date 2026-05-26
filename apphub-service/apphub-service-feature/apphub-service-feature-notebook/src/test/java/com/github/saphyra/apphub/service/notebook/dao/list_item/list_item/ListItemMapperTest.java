package com.github.saphyra.apphub.service.notebook.dao.list_item.list_item;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_ARCHIVED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PARENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PINNED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TITLE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_TYPE;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;
import static org.assertj.core.api.Assertions.assertThat;

class ListItemMapperTest {
    private static final String USER_ID = "user-id";
    private static final String LIST_ITEM_ID = "list-item-id";
    private static final String PARENT = "parent-id";
    private static final String TYPE = "TEXT";
    private static final String TITLE = "encrypted-title";
    private static final String PINNED = "encrypted-pinned";
    private static final String ARCHIVED = "encrypted-archived";
    private static final String DATA = "encrypted-data";

    private final ListItemMapper underTest = new ListItemMapper();

    @Test
    void convertDomain_withParent() {
        ListItemEntity entity = ListItemEntity.builder()
            .userId(USER_ID)
            .listItemId(LIST_ITEM_ID)
            .parent(PARENT)
            .type(TYPE)
            .title(TITLE)
            .pinned(PINNED)
            .archived(ARCHIVED)
            .data(DATA)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(entity);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_USER + USER_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_LIST_ITEM + LIST_ITEM_ID);
        assertThat(result.get(COLUMN_PARENT).s()).isEqualTo(PREFIX_LIST_ITEM + PARENT);
        assertThat(result.get(COLUMN_TYPE).s()).isEqualTo(TYPE);
        assertThat(result.get(COLUMN_TITLE).s()).isEqualTo(TITLE);
        assertThat(result.get(COLUMN_PINNED).s()).isEqualTo(PINNED);
        assertThat(result.get(COLUMN_ARCHIVED).s()).isEqualTo(ARCHIVED);
        assertThat(result.get(COLUMN_DATA).s()).isEqualTo(DATA);
    }

    @Test
    void convertDomain_nullParent() {
        ListItemEntity entity = ListItemEntity.builder()
            .userId(USER_ID)
            .listItemId(LIST_ITEM_ID)
            .parent(null)
            .type(TYPE)
            .title(TITLE)
            .pinned(PINNED)
            .archived(ARCHIVED)
            .data(DATA)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(entity);

        assertThat(result.get(COLUMN_PARENT).s()).isEqualTo(PREFIX_LIST_ITEM);
    }

    @Test
    void convertEntity_withParent() {
        Map<String, AttributeValue> entity = new HashMap<>();
        entity.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + USER_ID).build());
        entity.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + LIST_ITEM_ID).build());
        entity.put(COLUMN_PARENT, AttributeValue.builder().s(PREFIX_LIST_ITEM + PARENT).build());
        entity.put(COLUMN_TYPE, AttributeValue.builder().s(TYPE).build());
        entity.put(COLUMN_TITLE, AttributeValue.builder().s(TITLE).build());
        entity.put(COLUMN_PINNED, AttributeValue.builder().s(PINNED).build());
        entity.put(COLUMN_ARCHIVED, AttributeValue.builder().s(ARCHIVED).build());
        entity.put(COLUMN_DATA, AttributeValue.builder().s(DATA).build());

        ListItemEntity result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getType()).isEqualTo(TYPE);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getPinned()).isEqualTo(PINNED);
        assertThat(result.getArchived()).isEqualTo(ARCHIVED);
        assertThat(result.getData()).isEqualTo(DATA);
    }

    @Test
    void convertEntity_blankParent() {
        Map<String, AttributeValue> entity = new HashMap<>();
        entity.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_USER + USER_ID).build());
        entity.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + LIST_ITEM_ID).build());
        entity.put(COLUMN_PARENT, AttributeValue.builder().s(PREFIX_LIST_ITEM).build());
        entity.put(COLUMN_TYPE, AttributeValue.builder().s(TYPE).build());
        entity.put(COLUMN_TITLE, AttributeValue.builder().s(TITLE).build());
        entity.put(COLUMN_PINNED, AttributeValue.builder().s(PINNED).build());
        entity.put(COLUMN_ARCHIVED, AttributeValue.builder().s(ARCHIVED).build());
        entity.put(COLUMN_DATA, AttributeValue.builder().s(DATA).build());

        ListItemEntity result = underTest.convertEntity(entity);

        assertThat(result.getParent()).isNull();
    }
}