package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_COLUMNS;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_ROW;
import static org.assertj.core.api.Assertions.assertThat;

class TableRowMapperTest {
    private static final String LIST_ITEM_ID = "list-item-id";
    private static final String TABLE_ROW_ID = "table-row-id";
    private static final String ENCRYPTED_INDEX = "encrypted-index";
    private static final String ENCRYPTED_CHECKED = "encrypted-checked";
    private static final String ENCRYPTED_COLUMNS = "encrypted-columns";

    private final TableRowMapper underTest = new TableRowMapper();

    @Test
    void convertDomain() {
        TableRowEntity domain = TableRowEntity.builder()
            .listItemId(LIST_ITEM_ID)
            .tableRowId(TABLE_ROW_ID)
            .index(ENCRYPTED_INDEX)
            .checked(ENCRYPTED_CHECKED)
            .columns(ENCRYPTED_COLUMNS)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_LIST_ITEM + LIST_ITEM_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_TABLE_ROW + TABLE_ROW_ID);
        assertThat(result.get(COLUMN_INDEX).s()).isEqualTo(ENCRYPTED_INDEX);
        assertThat(result.get(COLUMN_CHECKED).s()).isEqualTo(ENCRYPTED_CHECKED);
        assertThat(result.get(COLUMN_COLUMNS).s()).isEqualTo(ENCRYPTED_COLUMNS);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + LIST_ITEM_ID).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_ROW + TABLE_ROW_ID).build(),
            COLUMN_INDEX, AttributeValue.builder().s(ENCRYPTED_INDEX).build(),
            COLUMN_CHECKED, AttributeValue.builder().s(ENCRYPTED_CHECKED).build(),
            COLUMN_COLUMNS, AttributeValue.builder().s(ENCRYPTED_COLUMNS).build()
        );

        TableRowEntity result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getTableRowId()).isEqualTo(TABLE_ROW_ID);
        assertThat(result.getIndex()).isEqualTo(ENCRYPTED_INDEX);
        assertThat(result.getChecked()).isEqualTo(ENCRYPTED_CHECKED);
        assertThat(result.getColumns()).isEqualTo(ENCRYPTED_COLUMNS);
    }
}

