package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TableActions {
    public static void createTable(int serverPort, UUID accessTokenId, CreateTableRequest request) {
        Response response = getCreateTableResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateTableResponse(int serverPort, UUID accessTokenId, CreateTableRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_TABLE));
    }

    public static EditTableResponse editTable(int serverPort, UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        Response response = getEditTableResponse(serverPort, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(EditTableResponse.class);
    }

    public static Response getEditTableResponse(int serverPort, UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editTableRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));
    }

    public static TableResponse getTable(int serverPort, UUID accessTokenId, UUID listItemId) {
        Response response = getTableResponse(serverPort, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TableResponse.class);
    }

    public static Response getTableResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_TABLE, "listItemId", listItemId));
    }

    public static void updateChecklistTableRowStatus(int serverPort, UUID accessTokenId, UUID rowId, Boolean status) {
        Response response = getUpdateChecklistTableRowStatusResponse(serverPort, accessTokenId, rowId, status);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistTableRowStatusResponse(int serverPort, UUID accessTokenId, UUID rowId, Boolean status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_TABLE_SET_ROW_STATUS, "rowId", rowId));
    }

    public static void editCheckboxStatus(int serverPort, UUID accessTokenId, UUID columnId, Boolean status) {
        Response response = getEditCheckboxStatusResponse(serverPort, accessTokenId, columnId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditCheckboxStatusResponse(int serverPort, UUID accessTokenId, UUID columnId, Boolean status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS, "columnId", columnId));
    }

    public static Response getDeleteCheckedResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_TABLE_DELETE_CHECKED, "listItemId", listItemId));
    }
}
