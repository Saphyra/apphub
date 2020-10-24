package com.github.saphyra.apphub.service.notebook.service.checklist_table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.edition.TableEditionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChecklistTableEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private TableEditionService tableEditionService;

    @Mock
    private EditTableRequestConverter editTableRequestConverter;

    @Mock
    private ChecklistTableRowDao checklistTableRowDao;

    @Mock
    private ChecklistTableRowFactory checklistTableRowFactory;

    @InjectMocks
    private ChecklistTableEditionService underTest;

    @Mock
    private EditChecklistTableRequest editChecklistTableRequest;

    @Mock
    private EditTableRequest editTableRequest;

    @Mock
    private ListItem listItem;

    @Mock
    private ChecklistTableRow checklistTableRow;

    @Test
    public void rowAlreadyPresent() {
        given(editTableRequestConverter.convert(editChecklistTableRequest)).willReturn(editTableRequest);
        given(tableEditionService.edit(LIST_ITEM_ID, editTableRequest)).willReturn(listItem);
        given(editChecklistTableRequest.getRows()).willReturn(Arrays.asList(ChecklistTableRowRequest.<KeyValuePair<String>>builder().checked(true).build()));
        given(checklistTableRowDao.findByParentAndRowIndex(LIST_ITEM_ID, 0)).willReturn(Optional.of(checklistTableRow));

        underTest.edit(LIST_ITEM_ID, editChecklistTableRequest);

        verify(checklistTableRow).setChecked(true);
        verify(checklistTableRowDao).save(checklistTableRow);
        verify(checklistTableRowDao).deleteByParentAndRowIndexGreaterThanEqual(LIST_ITEM_ID, 1);
    }

    @Test
    public void newRow() {
        given(listItem.getUserId()).willReturn(USER_ID);
        given(editTableRequestConverter.convert(editChecklistTableRequest)).willReturn(editTableRequest);
        given(tableEditionService.edit(LIST_ITEM_ID, editTableRequest)).willReturn(listItem);
        given(editChecklistTableRequest.getRows()).willReturn(Arrays.asList(ChecklistTableRowRequest.<KeyValuePair<String>>builder().checked(true).build()));
        given(checklistTableRowDao.findByParentAndRowIndex(LIST_ITEM_ID, 0)).willReturn(Optional.empty());
        given(checklistTableRowFactory.create(USER_ID, LIST_ITEM_ID, 0, true)).willReturn(checklistTableRow);

        underTest.edit(LIST_ITEM_ID, editChecklistTableRequest);

        verify(checklistTableRowDao).save(checklistTableRow);
        verify(checklistTableRowDao).deleteByParentAndRowIndexGreaterThanEqual(LIST_ITEM_ID, 1);
    }
}