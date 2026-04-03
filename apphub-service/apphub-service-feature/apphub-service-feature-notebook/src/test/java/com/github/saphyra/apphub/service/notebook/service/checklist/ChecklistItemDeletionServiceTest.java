package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistItemDeletionServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private CheckedItemDao checkedItemDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ChecklistItemDeletionService underTest;

    @Mock
    private Dimension checklistItem;

    @Test
    void deleteChecklistItem() {
        given(dimensionDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checklistItem);
        given(checklistItem.getDimensionId()).willReturn(CHECKLIST_ITEM_ID);

        underTest.deleteChecklistItem(CHECKLIST_ITEM_ID);

        then(dimensionDao).should().delete(checklistItem);
        then(contentDao).should().deleteByParent(CHECKLIST_ITEM_ID);
        then(checkedItemDao).should().deleteById(CHECKLIST_ITEM_ID);
    }
}