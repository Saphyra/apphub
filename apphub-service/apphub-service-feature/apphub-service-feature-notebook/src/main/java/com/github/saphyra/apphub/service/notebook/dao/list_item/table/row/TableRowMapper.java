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
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_ROW;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@Slf4j
//TODO unit test
class TableRowMapper extends ConverterBase<Map<String, AttributeValue>, TableRowEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(TableRowEntity domain) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build());
        item.put(COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId() + "|" + PREFIX_TABLE_ROW + domain.getTableRowId()).build());
        item.put(COLUMN_INDEX, AttributeValue.builder().s(domain.getIndex()).build());
        item.put(COLUMN_COLUMNS, AttributeValue.builder().s(domain.getColumns()).build());
        item.put(COLUMN_CHECKED, AttributeValue.builder().s(domain.getChecked()).build());

        return item;
    }

    @Override
    protected TableRowEntity processEntityConversion(Map<String, AttributeValue> entity) {
        String sk = entity.get(COLUMN_SK).s();
        String listItemId = sk.substring(PREFIX_LIST_ITEM.length(), sk.indexOf("|"));
        String tableRowId = sk.substring(sk.indexOf(PREFIX_TABLE_ROW) + PREFIX_TABLE_ROW.length());

        return TableRowEntity.builder()
            .userId(entity.get(COLUMN_USER_ID).s().substring(PREFIX_USER.length()))
            .listItemId(listItemId)
            .tableRowId(tableRowId)
            .index(entity.get(COLUMN_INDEX).s())
            .checked(entity.get(COLUMN_CHECKED).s())
            .columns(entity.get(COLUMN_COLUMNS).s())
            .build();
    }
}
