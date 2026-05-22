package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableCheckboxTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";

    @Test(groups = {"be", "notebook"})
    public void customTableCheckboxCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullChecked(accessToken);
        create(accessToken);

        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .getFirst()
            .getId();
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        edit(accessToken, listItemId, tableResponse);

        setStatus_nullStatus(accessToken, listItemId, tableResponse);
        setStatus(accessToken, listItemId, tableResponse);
        setStatus_notCheckboxColumn(accessToken, listItemId, tableResponse);

        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);
    }

    private void setStatus_notCheckboxColumn(String accessToken, UUID listItemId, TableResponse tableResponse) {
        UUID columnId = tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId();
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            columnId,
            ColumnType.EMPTY,
            null
        );

        tableResponse = TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest)
            .getTableResponse();

        TableRowModel row = tableResponse.getRows().getFirst();
        Response response = TableActions.getEditCheckboxStatusResponse(getServerPort(), accessToken, listItemId, row.getRowId(), row.getColumns().getFirst().getColumnId(), false);

        ResponseValidator.verifyInvalidParam(response, "columnId", "not a " + ColumnType.CHECKBOX);
    }

    private void setStatus(String accessToken, UUID listItemId, TableResponse tableResponse) {
        TableRowModel row = tableResponse.getRows().getFirst();
        TableActions.editCheckboxStatus(getServerPort(), accessToken,listItemId, row.getRowId(), row.getColumns().getFirst().getColumnId(), false);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(String.valueOf(false));
    }

    private void setStatus_nullStatus(String accessToken, UUID listItemId, TableResponse tableResponse) {
        TableRowModel row = tableResponse.getRows().getFirst();

        Response response = TableActions.getEditCheckboxStatusResponse(getServerPort(), accessToken, listItemId, row.getRowId(), row.getColumns().getFirst().getColumnId(), null);

        ResponseValidator.verifyInvalidParam(response, "status", "must not be null");
    }

    private void edit(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.CHECKBOX,
            Boolean.TRUE
        );

        TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(NEW_COLUMN_TITLE);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(Boolean.TRUE.toString());
    }

    private void create(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.CHECKBOX, Boolean.FALSE);

        TableActions.createTable(getServerPort(), accessToken, request);
    }

    private static void create_nullChecked(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.CHECKBOX, null);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "data", "must not be null");
    }
}
