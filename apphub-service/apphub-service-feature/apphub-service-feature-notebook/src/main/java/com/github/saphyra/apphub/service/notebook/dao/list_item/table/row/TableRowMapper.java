package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_COLUMNS;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_ROW;

@Component
@Slf4j
class TableRowMapper extends ConverterBase<Map<String, AttributeValue>, TableRowEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(TableRowEntity domain) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId()).build());
        item.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_ROW + domain.getTableRowId()).build());
        item.put(COLUMN_INDEX, AttributeValue.builder().s(domain.getIndex()).build());
        item.put(COLUMN_COLUMNS, AttributeValue.builder().s(domain.getColumns()).build());
        item.put(COLUMN_CHECKED, AttributeValue.builder().s(domain.getChecked()).build());

        return item;
    }

    @Override
    protected TableRowEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return TableRowEntity.builder()
            .listItemId(entity.get(COLUMN_PK).s().substring(PREFIX_LIST_ITEM.length()))
            .tableRowId(entity.get(COLUMN_SK).s().substring(PREFIX_TABLE_ROW.length()))
            .index(entity.get(COLUMN_INDEX).s())
            .checked(entity.get(COLUMN_CHECKED).s())
            .columns(entity.get(COLUMN_COLUMNS).s())
            .build();
    }
}
