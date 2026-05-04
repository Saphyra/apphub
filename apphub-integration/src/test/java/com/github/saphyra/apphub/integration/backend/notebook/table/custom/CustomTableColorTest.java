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

public class CustomTableColorTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";
    private static final String COLOR_CODE = "#ffffff";

    @Test(groups = {"be", "notebook"})
    public void customTableColorCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullColor(accessToken);
        create_invalidLength(accessToken);
        create_doesNotStartWithHashtag(accessToken);
        create_invalidCharacter(accessToken);
        create(accessToken);

        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        edit(accessToken, listItemId, tableResponse);

        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);
    }

    private void edit(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.COLOR,
            COLOR_CODE
        );

        TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(COLOR_CODE);
    }

    private void create(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.COLOR, "");

        TableActions.createTable(getServerPort(), accessToken, request);
    }

    private void create_invalidCharacter(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.COLOR, "#01234g");

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "color", "failed to parse");
    }

    private void create_doesNotStartWithHashtag(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.COLOR, "asdasda");

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "color", "first character is not #");
    }

    private void create_invalidLength(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.COLOR, "asd");

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "color", "must be 7 character(s) long");
    }

    private void create_nullColor(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.COLOR, null);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "color", "must not be null");
    }
}
