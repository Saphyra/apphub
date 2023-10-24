package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteCheckedItemsOfChecklistServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistQueryService checklistQueryService;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private ChecklistItemDeletionService checklistItemDeletionService;

    @InjectMocks
    private DeleteCheckedItemsOfChecklistService underTest;

    @Mock
    private Dimension dimension;

    @Mock
    private CheckedItem checkedItem;

    @Mock
    private ChecklistResponse checklistResponse;

    @Test
    void deleteCheckedItems() {
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(dimension));
        given(dimension.getDimensionId()).willReturn(CHECKLIST_ITEM_ID);
        given(checkedItemDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checkedItem);
        given(checkedItem.getChecked()).willReturn(true);
        given(checklistQueryService.getChecklistResponse(LIST_ITEM_ID)).willReturn(checklistResponse);
        given(checkedItem.getCheckedItemId()).willReturn(CHECKLIST_ITEM_ID);

        ChecklistResponse result = underTest.deleteCheckedItems(LIST_ITEM_ID);

        then(checklistItemDeletionService).should().deleteChecklistItem(CHECKLIST_ITEM_ID);

        assertThat(result).isEqualTo(checklistResponse);
    }
}