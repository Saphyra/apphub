package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TableCloneServiceTest {
    @Mock
    private TableHeadCloneService tableHeadCloneService;

    @Mock
    private TableRowCloneService tableRowCloneService;

    @InjectMocks
    private TableCloneService underTest;

    @Mock
    private ListItem original;

    @Mock
    private ListItem clone;

    @Test
    void cloneTable() {
        underTest.cloneTable(original, clone);

        then(tableHeadCloneService).should().cloneTableHeads(original, clone);
        then(tableRowCloneService).should().cloneRows(original, clone);
    }
}