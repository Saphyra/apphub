package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.TableEditionService;
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
public class ChecklistTableEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();

    @Mock
    private TableEditionService tableEditionService;

    @Mock
    private EditChecklistTableRowDeletionService editChecklistTableRowDeletionService;

    @Mock
    private EditChecklistTableCreateAndUpdateRowService editChecklistTableCreateAndUpdateRowService;

    @InjectMocks
    private ChecklistTableEditionService underTest;

    @Mock
    private EditChecklistTableRequest editChecklistTableRequest;

    @Mock
    private EditTableRequest editTableRequest;

    @Mock
    private ListItem listItem;

    @Mock
    private EditChecklistTableRowRequest editChecklistTableRowRequest;

    @Test
    void edit() {
        given(editChecklistTableRequest.getTable()).willReturn(editTableRequest);
        given(tableEditionService.edit(LIST_ITEM_ID, editTableRequest)).willReturn(listItem);
        given(editChecklistTableRequest.getRows()).willReturn(List.of(editChecklistTableRowRequest));

        underTest.edit(LIST_ITEM_ID, editChecklistTableRequest);

        then(editChecklistTableRowDeletionService).should().deleteChecklistTableRows(List.of(editChecklistTableRowRequest), LIST_ITEM_ID);
        then(editChecklistTableCreateAndUpdateRowService).should().createAndUpdate(List.of(editChecklistTableRowRequest), listItem);
    }
}