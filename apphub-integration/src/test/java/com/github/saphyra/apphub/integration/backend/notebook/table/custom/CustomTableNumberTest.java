package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.Number;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableNumberTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";

    @Test(groups = {"be", "notebook"})
    public void customTableNumberCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullData(accessTokenId);
        create_failedToParse(accessTokenId);
        create_nullNumberValue(accessTokenId);
        create_nullNumberStep(accessTokenId);
        create_tooLowStep(accessTokenId);
        create(accessTokenId);

        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessTokenId, listItemId);

        edit(accessTokenId, listItemId, tableResponse);
    }

    private void edit(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        Number number = new Number(2d, 1d);

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.NUMBER,
            number
        );

        TableActions.editTable(getServerPort(), accessTokenId, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(getServerPort(), accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        Number data = OBJECT_MAPPER_WRAPPER.convertValue(tableResponse.getRows().get(0).getColumns().get(0).getData(), Number.class);
        assertThat(data.getValue()).isEqualTo(1d);
        assertThat(data.getStep()).isEqualTo(2d);
    }

    private void create(UUID accessTokenId) {
        Number number = new Number(32d, 45d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, number);

        TableActions.createTable(getServerPort(), accessTokenId, request);
    }

    private void create_tooLowStep(UUID accessTokenId) {
        Number number = new Number(0d, 23d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, number);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "number.step", "too low");
    }

    private void create_nullNumberStep(UUID accessTokenId) {
        Number number = new Number(null, 23d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, number);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "number.step", "must not be null");
    }

    private void create_nullNumberValue(UUID accessTokenId) {
        Number number = new Number(32d, null);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, number);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "number.value", "must not be null");
    }

    private void create_failedToParse(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, "asd");

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "number", "failed to parse");
    }

    private void create_nullData(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, null);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "number", "must not be null");
    }
}
