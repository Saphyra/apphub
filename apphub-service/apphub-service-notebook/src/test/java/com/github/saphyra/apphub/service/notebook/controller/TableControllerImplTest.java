package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.table.CheckedTableRowDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.TableRowStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.edit.TableEditionService;
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
class TableControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();

    @Mock
    private TableCreationService tableCreationService;

    @Mock
    private TableQueryService tableQueryService;

    @Mock
    private TableRowStatusUpdateService tableRowStatusUpdateService;

    @Mock
    private CheckedTableRowDeletionService checkedTableRowDeletionService;

    @Mock
    private TableEditionService tableEditionService;

    @InjectMocks
    private TableControllerImpl underTest;

    @Mock
    private CreateTableRequest createTableRequest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private TableFileUploadResponse fileUploadResponse;

    @Mock
    private EditTableResponse editTableResponse;

    @Mock
    private EditTableRequest editTableRequest;

    @Mock
    private TableResponse tableResponse;

    @Test
    void createTable() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
        given(tableCreationService.create(USER_ID, createTableRequest)).willReturn(List.of(fileUploadResponse));

        assertThat(underTest.createTable(createTableRequest, accessTokenHeader)).containsExactly(fileUploadResponse);
    }

    @Test
    void editTable() {
        given(tableEditionService.editTable(LIST_ITEM_ID, editTableRequest)).willReturn(editTableResponse);

        assertThat(underTest.editTable(editTableRequest, LIST_ITEM_ID, accessTokenHeader)).isEqualTo(editTableResponse);
    }

    @Test
    void getTable() {
        given(tableQueryService.getTable(LIST_ITEM_ID)).willReturn(tableResponse);

        assertThat(underTest.getTable(LIST_ITEM_ID, accessTokenHeader)).isEqualTo(tableResponse);
    }

    @Test
    void setRowStatus() {
        underTest.setRowStatus(ROW_ID, new OneParamRequest<>(true), accessTokenHeader);

        then(tableRowStatusUpdateService).should().setRowStatus(ROW_ID, true);
    }

    @Test
    void deleteCheckedRows() {
        given(tableQueryService.getTable(LIST_ITEM_ID)).willReturn(tableResponse);

        assertThat(underTest.deleteCheckedRows(LIST_ITEM_ID, accessTokenHeader)).isEqualTo(tableResponse);

        then(checkedTableRowDeletionService).should().deleteCheckedRows(LIST_ITEM_ID);
    }
}