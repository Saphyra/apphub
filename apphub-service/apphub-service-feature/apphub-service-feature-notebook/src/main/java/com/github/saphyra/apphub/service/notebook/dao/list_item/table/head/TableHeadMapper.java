package com.github.saphyra.apphub.service.notebook.dao.list_item.table.head;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_DATA;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_TABLE_HEAD;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
//TODO unit test
class TableHeadMapper extends ConverterBase<Map<String, AttributeValue>, TableHeadEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(TableHeadEntity domain) {
        return Map.of(
            COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId() + "|" + PREFIX_TABLE_HEAD).build(),
            COLUMN_DATA, AttributeValue.builder().s(domain.getData()).build()
        );
    }

    @Override
    protected TableHeadEntity processEntityConversion(Map<String, AttributeValue> entity) {
        String sk = entity.get(COLUMN_SK).s();
        String listItemId = sk.substring(PREFIX_LIST_ITEM.length(), sk.indexOf("|"));

        return TableHeadEntity.builder()
            .userId(entity.get(COLUMN_USER_ID).s().substring(PREFIX_USER.length()))
            .listItemId(listItemId)
            .data(entity.get(COLUMN_DATA).s())
            .build();
    }
}
