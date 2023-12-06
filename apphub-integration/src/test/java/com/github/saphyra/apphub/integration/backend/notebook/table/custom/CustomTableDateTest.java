package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
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

public class CustomTableDateTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String DATE = "2023-12-15";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";

    @Test(groups = {"be", "notebook"})
    public void customTableDateCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        create_nullDate(accessTokenId);
        create_failedToParse(accessTokenId);
        create(accessTokenId);

        UUID listItemId = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(accessTokenId, listItemId);

        edit(accessTokenId, listItemId, tableResponse);
    }

    private void edit(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.DATE,
            ""
        );

        TableActions.editTable(accessTokenId, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo("");
    }

    private void create(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.DATE, DATE);

        TableActions.createTable(accessTokenId, request);
    }

    private void create_failedToParse(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.DATE, "asd");

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "date", "failed to parse");
    }

    private void create_nullDate(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.DATE, null);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "date", "must not be null");
    }
}
