package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.table.CheckboxColumnStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.table.CheckedTableRowDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.TableRowStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.table.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.deprecated_edit.DeprecatedTableEditionService;
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
    private static final UUID COLUMN_ID = UUID.randomUUID();

    @Mock
    private TableCreationService tableCreationService;

    @Mock
    private TableQueryService tableQueryService;

    @Mock
    private TableRowStatusUpdateService tableRowStatusUpdateService;

    @Mock
    private CheckedTableRowDeletionService checkedTableRowDeletionService;

    @Mock
    private DeprecatedTableEditionService tableEditionService;

    @Mock
    private CheckboxColumnStatusUpdateService checkboxColumnStatusUpdateService;

    @InjectMocks
    private TableControllerImpl underTest;

    @Mock
    private CreateTableRequest createTableRequest;

    @Mock
    private AccessToken accessToken;

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
        given(accessToken.getUserId()).willReturn(USER_ID);
        given(tableCreationService.create(USER_ID, createTableRequest)).willReturn(List.of(fileUploadResponse));

        assertThat(underTest.createTable(createTableRequest, accessToken)).containsExactly(fileUploadResponse);
    }

    @Test
    void editTable() {
        given(tableEditionService.editTable(LIST_ITEM_ID, editTableRequest)).willReturn(editTableResponse);

        assertThat(underTest.editTable(editTableRequest, LIST_ITEM_ID, accessToken)).isEqualTo(editTableResponse);
    }

    @Test
    void getTable() {
        given(tableQueryService.getTable(LIST_ITEM_ID)).willReturn(tableResponse);

        assertThat(underTest.getTable(LIST_ITEM_ID, accessToken)).isEqualTo(tableResponse);
    }

    @Test
    void setRowStatus() {
        underTest.setRowStatus(ROW_ID, new OneParamRequest<>(true), accessToken);

        then(tableRowStatusUpdateService).should().setRowStatus(ROW_ID, true);
    }

    @Test
    void deleteCheckedRows() {
        given(tableQueryService.getTable(LIST_ITEM_ID)).willReturn(tableResponse);

        assertThat(underTest.deleteCheckedRows(LIST_ITEM_ID, accessToken)).isEqualTo(tableResponse);

        then(checkedTableRowDeletionService).should().deleteCheckedRows(LIST_ITEM_ID);
    }

    @Test
    void setCheckboxColumnStatus(){
        underTest.setCheckboxColumnStatus(COLUMN_ID, new OneParamRequest<>(true), accessToken);

        then(checkboxColumnStatusUpdateService).should().updateColumnStatus(COLUMN_ID, true);
    }
}