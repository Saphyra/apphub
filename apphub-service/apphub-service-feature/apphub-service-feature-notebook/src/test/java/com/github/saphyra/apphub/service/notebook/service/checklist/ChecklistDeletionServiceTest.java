package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistDeletionServiceTest {
    @Mock
    private CommonListItemDao commonListItemDao;

    @InjectMocks
    private ChecklistDeletionService underTest;

    @Mock
    private ListItem listItem;

    @Test
    void delete() {
        underTest.delete(listItem);

        then(commonListItemDao).should().delete(listItem);
    }
}