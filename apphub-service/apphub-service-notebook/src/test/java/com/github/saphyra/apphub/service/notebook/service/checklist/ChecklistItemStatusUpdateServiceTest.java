package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistItemStatusUpdateServiceTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private CheckedItemDao checkedItemDao;

    @InjectMocks
    private ChecklistItemStatusUpdateService underTest;

    @Mock
    private CheckedItem checkedItem;

    @Test
    void updateStatus() {
        given(checkedItemDao.findByIdValidated(CHECKLIST_ITEM_ID)).willReturn(checkedItem);

        underTest.updateStatus(CHECKLIST_ITEM_ID, true);

        then(checkedItem).should().setChecked(true);
        then(checkedItemDao).should().save(checkedItem);
    }
}