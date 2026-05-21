package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_USER_ID;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_USER;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistItemMapper extends ConverterBase<Map<String, AttributeValue>, ChecklistItemEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(ChecklistItemEntity domain) {
        return Map.of(
            COLUMN_USER_ID, AttributeValue.builder().s(PREFIX_USER + domain.getUserId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId() + "|" + PREFIX_CHECKLIST_ITEM + domain.getChecklistItemId()).build(),
            COLUMN_CHECKED, AttributeValue.builder().s(domain.getChecked()).build(),
            COLUMN_INDEX, AttributeValue.builder().s(domain.getIndex()).build()
        );
    }

    @Override
    protected ChecklistItemEntity processEntityConversion(Map<String, AttributeValue> entity) {
        String sk = entity.get(COLUMN_SK).s();
        String listItemId = sk.substring(PREFIX_LIST_ITEM.length(), sk.indexOf("|"));
        String checklistItemId = sk.substring(sk.indexOf(PREFIX_CHECKLIST_ITEM) + PREFIX_CHECKLIST_ITEM.length());

        return ChecklistItemEntity.builder()
            .userId(entity.get(COLUMN_USER_ID).s().substring(PREFIX_USER.length()))
            .listItemId(listItemId)
            .checklistItemId(checklistItemId)
            .checked(entity.get(COLUMN_CHECKED).s())
            .index(entity.get(COLUMN_INDEX).s())
            .build();
    }
}
