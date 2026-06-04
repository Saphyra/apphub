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

public class CustomTableTextTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";
    private static final String TEXT = "text";

    @Test(groups = {"be", "notebook"})
    public void customTableTextCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullText(accessToken);
        create(accessToken);

        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .getFirst()
            .getId();
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        edit(accessToken, listItemId, tableResponse);

        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);
    }

    private void edit(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.TEXT,
            ""
        );

        TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(NEW_COLUMN_TITLE);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo("");
    }

    private void create(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.TEXT, TEXT);

        TableActions.createTable(getServerPort(), accessToken, request);
    }

    private static void create_nullText(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.TEXT, null);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "data", "must not be null");
    }
}
