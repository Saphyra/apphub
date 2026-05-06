package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.Range;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableRangeTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";

    @Test(groups = {"be", "notebook"})
    public void customTableNumberCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullData(accessToken);
        create_failedToParse(accessToken);
        create_nullValue(accessToken);
        create_nullStep(accessToken);
        create_tooLowStep(accessToken);
        create_nullMin(accessToken);
        create_nullMax(accessToken);
        create_tooLowMax(accessToken);
        create_valueNotInRange(accessToken);
        create(accessToken);

        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        edit(accessToken, listItemId, tableResponse);
    }

    private void edit(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Range number = new Range(5d, 10d, 7d, 8d);

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.RANGE,
            number
        );

        TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        Range data = OBJECT_MAPPER_WRAPPER.convertValue(tableResponse.getRows().get(0).getColumns().get(0).getData(), Range.class);
        assertThat(data.getValue()).isEqualTo(8d);
        assertThat(data.getStep()).isEqualTo(7d);
        assertThat(data.getMin()).isEqualTo(5d);
        assertThat(data.getMax()).isEqualTo(10d);
    }

    private void create(String accessToken) {
        Range range = new Range(1d, 3d, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, range);

        TableActions.createTable(getServerPort(), accessToken, request);
    }

    private void create_valueNotInRange(String accessToken) {
        Range range = new Range(1d, 3d, 3d, 0d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.value", "too low");
    }

    private void create_tooLowMax(String accessToken) {
        Range range = new Range(2d, 1d, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.max", "too low");
    }

    private void create_nullMax(String accessToken) {
        Range range = new Range(1d, null, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.max", "must not be null");
    }

    private void create_nullMin(String accessToken) {
        Range range = new Range(null, 3d, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.min", "must not be null");
    }

    private void create_tooLowStep(String accessToken) {
        Range range = new Range(1d, 3d, 0d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.step", "too low");
    }

    private void create_nullStep(String accessToken) {
        Range range = new Range(1d, 3d, null, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.step", "must not be null");
    }

    private void create_nullValue(String accessToken) {
        Range range = new Range(1d, 3d, 4d, null);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range.value", "must not be null");
    }

    private void create_failedToParse(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, "asd");

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range", "failed to parse");
    }

    private void create_nullData(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, null);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "range", "must not be null");
    }
}
