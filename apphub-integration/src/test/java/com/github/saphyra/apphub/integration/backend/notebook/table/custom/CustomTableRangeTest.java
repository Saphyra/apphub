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
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        create_nullData(accessTokenId);
        create_failedToParse(accessTokenId);
        create_nullValue(accessTokenId);
        create_nullStep(accessTokenId);
        create_tooLowStep(accessTokenId);
        create_nullMin(accessTokenId);
        create_nullMax(accessTokenId);
        create_tooLowMax(accessTokenId);
        create_valueNotInRange(accessTokenId);
        create(accessTokenId);

        UUID listItemId = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(accessTokenId, listItemId);

        edit(accessTokenId, listItemId, tableResponse);
    }

    private void edit(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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

        TableActions.editTable(accessTokenId, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        Range data = OBJECT_MAPPER_WRAPPER.convertValue(tableResponse.getRows().get(0).getColumns().get(0).getData(), Range.class);
        assertThat(data.getValue()).isEqualTo(8d);
        assertThat(data.getStep()).isEqualTo(7d);
        assertThat(data.getMin()).isEqualTo(5d);
        assertThat(data.getMax()).isEqualTo(10d);
    }

    private void create(UUID accessTokenId) {
        Range range = new Range(1d, 3d, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.NUMBER, range);

        TableActions.createTable(accessTokenId, request);
    }

    private void create_valueNotInRange(UUID accessTokenId) {
        Range range = new Range(1d, 3d, 3d, 0d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.value", "too low");
    }

    private void create_tooLowMax(UUID accessTokenId) {
        Range range = new Range(2d, 1d, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.max", "too low");
    }

    private void create_nullMax(UUID accessTokenId) {
        Range range = new Range(1d, null, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.max", "must not be null");
    }

    private void create_nullMin(UUID accessTokenId) {
        Range range = new Range(null, 3d, 3d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.min", "must not be null");
    }

    private void create_tooLowStep(UUID accessTokenId) {
        Range range = new Range(1d, 3d, 0d, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.step", "too low");
    }

    private void create_nullStep(UUID accessTokenId) {
        Range range = new Range(1d, 3d, null, 2d);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.step", "must not be null");
    }

    private void create_nullValue(UUID accessTokenId) {
        Range range = new Range(1d, 3d, 4d, null);
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, range);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range.value", "must not be null");
    }

    private void create_failedToParse(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, "asd");

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range", "failed to parse");
    }

    private void create_nullData(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.RANGE, null);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "range", "must not be null");
    }
}
