package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistItemCreationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditChecklistRowSaverTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemCreationService checklistItemCreationService;

    @Mock
    private EditChecklistRowUpdater editChecklistRowUpdater;

    @InjectMocks
    private EditChecklistRowSaver underTest;

    @Mock
    private ChecklistItemModel model;

    @Test
    void saveExisting() {
        given(model.getType()).willReturn(ItemType.EXISTING);

        underTest.saveItems(USER_ID, LIST_ITEM_ID, List.of(model));

        then(editChecklistRowUpdater).should().updateExistingChecklistItem(model);
        then(checklistItemCreationService).shouldHaveNoInteractions();
    }

    @Test
    void saveNew() {
        given(model.getType()).willReturn(ItemType.NEW);

        underTest.saveItems(USER_ID, LIST_ITEM_ID, List.of(model));

        then(editChecklistRowUpdater).shouldHaveNoInteractions();
        then(checklistItemCreationService).should().create(USER_ID, LIST_ITEM_ID, model);
    }
}