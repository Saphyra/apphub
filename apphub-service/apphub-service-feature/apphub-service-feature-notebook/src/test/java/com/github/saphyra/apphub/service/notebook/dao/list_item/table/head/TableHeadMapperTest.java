package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_HEAD;
import static org.assertj.core.api.Assertions.assertThat;

class TableHeadMapperTest {
    private static final String LIST_ITEM_ID = "list-item-id";
    private static final String DATA = "data";

    private final TableHeadMapper underTest = new TableHeadMapper();

    @Test
    void convertDomain() {
        TableHeadEntity domain = TableHeadEntity.builder()
            .listItemId(LIST_ITEM_ID)
            .data(DATA)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_LIST_ITEM + LIST_ITEM_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_TABLE_HEAD);
        assertThat(result.get(COLUMN_DATA).s()).isEqualTo(DATA);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + LIST_ITEM_ID).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_HEAD).build(),
            COLUMN_DATA, AttributeValue.builder().s(DATA).build()
        );

        TableHeadEntity result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getData()).isEqualTo(DATA);
    }
}

