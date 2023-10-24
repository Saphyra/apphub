package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemDeletionService;
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
class EditChecklistRowDeleterTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID EXISTING_CHECKLIST_ITEM_ID = UUID.randomUUID();
    public static final UUID TO_DELETE_CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ChecklistItemDeletionService checklistItemDeletionService;

    @InjectMocks
    private EditChecklistRowDeleter underTest;

    @Mock
    private ChecklistItemModel existingModel;

    @Mock
    private ChecklistItemModel newModel;

    @Mock
    private Dimension toKeepItem;

    @Mock
    private Dimension toDeleteItem;

    @Test
    void deleteRemovedItems() {
        given(existingModel.getType()).willReturn(ItemType.EXISTING);
        given(newModel.getType()).willReturn(ItemType.NEW);
        given(existingModel.getChecklistItemId()).willReturn(EXISTING_CHECKLIST_ITEM_ID);

        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(toKeepItem, toDeleteItem));
        given(toKeepItem.getDimensionId()).willReturn(EXISTING_CHECKLIST_ITEM_ID);
        given(toKeepItem.getDimensionId()).willReturn(TO_DELETE_CHECKLIST_ITEM_ID);

        underTest.deleteRemovedItems(LIST_ITEM_ID, List.of(existingModel, newModel));

        then(checklistItemDeletionService).should().deleteChecklistItem(TO_DELETE_CHECKLIST_ITEM_ID);
    }
}