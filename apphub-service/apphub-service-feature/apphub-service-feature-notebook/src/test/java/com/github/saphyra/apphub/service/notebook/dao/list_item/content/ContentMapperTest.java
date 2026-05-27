package com.github.saphyra.apphub.service.notebook.dao.list_item.content;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CONTENT;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static org.assertj.core.api.Assertions.assertThat;

class ContentMapperTest {
    private static final String LIST_ITEM_ID = "list-item-id";
    private static final Integer BATCH_INDEX = 3;
    private static final String ENCRYPTED_CONTENT = "encrypted-content";

    private final ContentMapper underTest = new ContentMapper();

    @Test
    void convertDomain() {
        ContentEntity domain = ContentEntity.builder()
            .listItemId(LIST_ITEM_ID)
            .batchIndex(BATCH_INDEX)
            .content(ENCRYPTED_CONTENT)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_LIST_ITEM + LIST_ITEM_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_CONTENT + BATCH_INDEX);
        assertThat(result.get(COLUMN_CONTENT).s()).isEqualTo(ENCRYPTED_CONTENT);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + LIST_ITEM_ID).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_CONTENT + BATCH_INDEX).build(),
            COLUMN_CONTENT, AttributeValue.builder().s(ENCRYPTED_CONTENT).build()
        );

        ContentEntity result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getBatchIndex()).isEqualTo(BATCH_INDEX);
        assertThat(result.getContent()).isEqualTo(ENCRYPTED_CONTENT);
    }
}

