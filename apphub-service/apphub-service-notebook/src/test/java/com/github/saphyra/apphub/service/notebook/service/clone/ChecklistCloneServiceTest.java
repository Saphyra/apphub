package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChecklistCloneServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private ChecklistItemDao checklistItemDao;

    @Mock
    private ChecklistItemCloneService checklistItemCloneService;

    @InjectMocks
    private ChecklistCloneService underTest;

    @Mock
    private ListItem original;

    @Mock
    private ListItem clone;

    @Mock
    private ChecklistItem checklistItem;

    @Test
    public void cloneTest() {
        given(original.getListItemId()).willReturn(LIST_ITEM_ID);
        given(checklistItemDao.getByParent(LIST_ITEM_ID)).willReturn(Arrays.asList(checklistItem));

        underTest.clone(original, clone);

        verify(checklistItemCloneService).clone(clone, checklistItem);
    }
}