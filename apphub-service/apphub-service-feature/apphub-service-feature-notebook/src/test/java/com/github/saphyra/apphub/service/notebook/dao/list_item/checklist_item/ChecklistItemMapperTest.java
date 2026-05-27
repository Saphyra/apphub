package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_CHECKED;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_INDEX;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_PK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.COLUMN_SK;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_CHECKLIST_ITEM;
import static com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDaoConstants.PREFIX_LIST_ITEM;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ChecklistItemMapperTest {
    private static final String LIST_ITEM_ID = "list-item-id";
    private static final String CHECKLIST_ITEM_ID = "checklist-item-id";
    private static final String CHECKED = "true";
    private static final String INDEX = "3";

    @InjectMocks
    private ChecklistItemMapper underTest;

    @Test
    void convertDomain() {
        ChecklistItemEntity domain = ChecklistItemEntity.builder()
            .listItemId(LIST_ITEM_ID)
            .checklistItemId(CHECKLIST_ITEM_ID)
            .checked(CHECKED)
            .index(INDEX)
            .build();

        Map<String, AttributeValue> result = underTest.convertDomain(domain);

        assertThat(result.get(COLUMN_PK).s()).isEqualTo(PREFIX_LIST_ITEM + LIST_ITEM_ID);
        assertThat(result.get(COLUMN_SK).s()).isEqualTo(PREFIX_CHECKLIST_ITEM + CHECKLIST_ITEM_ID);
        assertThat(result.get(COLUMN_CHECKED).s()).isEqualTo(CHECKED);
        assertThat(result.get(COLUMN_INDEX).s()).isEqualTo(INDEX);
    }

    @Test
    void convertEntity() {
        Map<String, AttributeValue> entity = Map.of(
            COLUMN_PK, AttributeValue.builder().s(PREFIX_LIST_ITEM + LIST_ITEM_ID).build(),
            COLUMN_SK, AttributeValue.builder().s(PREFIX_CHECKLIST_ITEM + CHECKLIST_ITEM_ID).build(),
            COLUMN_CHECKED, AttributeValue.builder().s(CHECKED).build(),
            COLUMN_INDEX, AttributeValue.builder().s(INDEX).build()
        );

        ChecklistItemEntity result = underTest.convertEntity(entity);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(result.getChecked()).isEqualTo(CHECKED);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }
}