package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TableActions {
    public static void createTable(int serverPort, String accessToken, CreateTableRequest request) {
        Response response = getCreateTableResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateTableResponse(int serverPort, String accessToken, CreateTableRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_TABLE));
    }

    public static EditTableResponse editTable(int serverPort, String accessToken, UUID listItemId, EditTableRequest editTableRequest) {
        Response response = getEditTableResponse(serverPort, accessToken, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(EditTableResponse.class);
    }

    public static Response getEditTableResponse(int serverPort, String accessToken, UUID listItemId, EditTableRequest editTableRequest) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));
    }

    public static TableResponse getTable(int serverPort, String accessToken, UUID listItemId) {
        Response response = getTableResponse(serverPort, accessToken, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TableResponse.class);
    }

    public static Response getTableResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_TABLE, "listItemId", listItemId));
    }

    public static void updateChecklistTableRowStatus(int serverPort, String accessToken, UUID listItemId, UUID rowId, Boolean status) {
        Response response = getUpdateChecklistTableRowStatusResponse(serverPort, accessToken, listItemId, rowId, status);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistTableRowStatusResponse(int serverPort, String accessToken, UUID listItemId, UUID rowId, Boolean status) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(
                serverPort,
                NotebookEndpoints.NOTEBOOK_TABLE_SET_ROW_STATUS,
                Map.of(
                    "listItemId", listItemId,
                    "rowId", rowId
                )
            ));
    }

    public static void editCheckboxStatus(int serverPort, String accessToken, UUID listItemId, UUID rowId, UUID columnId, Boolean status) {
        Response response = getEditCheckboxStatusResponse(serverPort, accessToken, listItemId, rowId, columnId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditCheckboxStatusResponse(int serverPort, String accessToken, UUID listItemId, UUID rowId, UUID columnId, Boolean status) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(
                serverPort,
                NotebookEndpoints.NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS,
                Map.of(
                    "listItemId", listItemId,
                    "rowId", rowId,
                    "columnId", columnId
                )
            ));
    }

    public static Response getDeleteCheckedResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_TABLE_DELETE_CHECKED, "listItemId", listItemId));
    }
}
