package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;

@Component
@RequiredArgsConstructor
@Slf4j
class ChecklistItemMapper extends ConverterBase<Map<String, AttributeValue>, ChecklistItemEntity> {
    @Override
    protected Map<String, AttributeValue> processDomainConversion(ChecklistItemEntity domain) {
        return Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + domain.getListItemId()).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + domain.getChecklistItemId()).build(),
            COLUMN_CHECKED, AttributeValue.builder().s(domain.getChecked()).build(),
            COLUMN_INDEX, AttributeValue.builder().s(domain.getIndex()).build()
        );
    }

    @Override
    protected ChecklistItemEntity processEntityConversion(Map<String, AttributeValue> entity) {
        return ChecklistItemEntity.builder()
            .listItemId(entity.get(COLUMN_PK).s().substring(PREFIX_LIST_ITEM.length()))
            .checklistItemId(entity.get(COLUMN_SK).s().substring(PREFIX_CHECKLIST_ITEM.length()))
            .checked(entity.get(COLUMN_CHECKED).s())
            .index(entity.get(COLUMN_INDEX).s())
            .build();
    }
}
