package com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChecklistItemFactoryTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final boolean CHECKED = true;
    private static final int INDEX = 3;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ChecklistItemFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(CHECKLIST_ITEM_ID);

        ChecklistItem result = underTest.create(LIST_ITEM_ID, CHECKED, INDEX);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(result.isChecked()).isEqualTo(CHECKED);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }

    @Test
    void clone_checklistItem() {
        given(idGenerator.randomUuid()).willReturn(CHECKLIST_ITEM_ID);

        ChecklistItem original = ChecklistItem.builder()
            .listItemId(UUID.randomUUID())
            .checklistItemId(UUID.randomUUID())
            .checked(CHECKED)
            .index(INDEX)
            .build();

        ChecklistItem result = underTest.clone(LIST_ITEM_ID, original);

        assertThat(result.getListItemId()).isEqualTo(LIST_ITEM_ID);
        assertThat(result.getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(result.isChecked()).isEqualTo(CHECKED);
        assertThat(result.getIndex()).isEqualTo(INDEX);
    }
}