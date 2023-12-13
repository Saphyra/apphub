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
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        create_nullChecked(accessTokenId);
        create(accessTokenId);

        UUID listItemId = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(accessTokenId, listItemId);

        edit(accessTokenId, listItemId, tableResponse);

        setStatus_nullStatus(accessTokenId, tableResponse);
        setStatus(accessTokenId, listItemId, tableResponse);
        setStatus_notCheckboxColumn(accessTokenId, listItemId, tableResponse);

        ListItemActions.deleteListItem(accessTokenId, listItemId);
    }

    private void setStatus_notCheckboxColumn(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        UUID columnId = tableResponse.getRows().get(0).getColumns().get(0).getColumnId();
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            columnId,
            ColumnType.EMPTY,
            null
        );

        tableResponse = TableActions.editTable(accessTokenId, listItemId, editTableRequest)
            .getTableResponse();

        Response response = TableActions.getEditCheckboxStatusResponse(accessTokenId, tableResponse.getRows().get(0).getColumns().get(0).getColumnId(), false);

        ResponseValidator.verifyInvalidParam(response, "columnId", "not a " + ColumnType.CHECKBOX);
    }

    private void setStatus(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        TableActions.editCheckboxStatus(accessTokenId, tableResponse.getRows().get(0).getColumns().get(0).getColumnId(), false);

        tableResponse = TableActions.getTable(accessTokenId, listItemId);

        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(String.valueOf(false));
    }

    private void setStatus_nullStatus(UUID accessTokenId, TableResponse tableResponse) {
        Response response = TableActions.getEditCheckboxStatusResponse(accessTokenId, tableResponse.getRows().get(0).getColumns().get(0).getColumnId(), null);

        ResponseValidator.verifyInvalidParam(response, "status", "must not be null");
    }

    private void edit(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.CHECKBOX,
            Boolean.TRUE
        );

        TableActions.editTable(accessTokenId, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(Boolean.TRUE.toString());
    }

    private void create(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.CHECKBOX, Boolean.FALSE);

        TableActions.createTable(accessTokenId, request);
    }

    private static void create_nullChecked(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.CHECKBOX, null);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "checked", "must not be null");
    }
}
