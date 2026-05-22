package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_HEAD;

@Component
//TODO unit test
class TableHeadMapper extends ConverterBase<Map<String, AttributeValue>, TableHeadEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(TableHeadEntity domain) {
        return Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_TABLE_HEAD).build(),
            COLUMN_DATA, AttributeValue.builder().s(domain.getData()).build()
        );
    }

    @Override
    protected TableHeadEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return TableHeadEntity.builder()
            .listItemId(entity.get(COLUMN_PK).s().substring(PREFIX_LIST_ITEM.length()))
            .data(entity.get(COLUMN_DATA).s())
            .build();
    }
}
