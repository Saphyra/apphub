package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.validator.EditTableRequestValidator;
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
class TableEditionServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";

    @Mock
    private EditTableRequestValidator editTableRequestValidator;

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableQueryService tableQueryService;

    @Mock
    private EditTableHeadService editTableHeadService;

    @Mock
    private EditTableRowService editTableRowService;

    @InjectMocks
    private TableEditionService underTest;

    @Mock
    private EditTableRequest editTableRequest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHeadModel tableHeadModel;

    @Mock
    private TableResponse tableResponse;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private TableRowModel tableRowModel;

    @Test
    void editTable() {
        given(editTableRequest.getTitle()).willReturn(TITLE);
        given(editTableRequest.getTableHeads()).willReturn(List.of(tableHeadModel));
        given(editTableRequest.getRows()).willReturn(List.of(tableRowModel));
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(editTableRowService.editTableRows(listItem, List.of(tableRowModel))).willReturn(List.of(fileUploadResponse));
        given(tableQueryService.getTable(LIST_ITEM_ID)).willReturn(tableResponse);

        EditTableResponse result = underTest.editTable(LIST_ITEM_ID, editTableRequest);

        assertThat(result.getFileUpload()).containsExactly(fileUploadResponse);
        assertThat(result.getTableResponse()).isEqualTo(tableResponse);

        then(editTableRequestValidator).should().validate(LIST_ITEM_ID, editTableRequest);
        then(listItem).should().setTitle(TITLE);
        then(listItemDao).should().save(listItem);
        then(editTableHeadService).should().editTableHeads(listItem, List.of(tableHeadModel));
    }
}