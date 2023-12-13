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

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TableActions {
    public static void createTable(UUID accessTokenId, CreateTableRequest request) {
        Response response = getCreateTableResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCreateTableResponse(UUID accessTokenId, CreateTableRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_TABLE));
    }

    public static EditTableResponse editTable(UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        Response response = getEditTableResponse(accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody()
            .as(EditTableResponse.class);
    }

    public static Response getEditTableResponse(UUID accessTokenId, UUID listItemId, EditTableRequest editTableRequest) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editTableRequest)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_EDIT_TABLE, "listItemId", listItemId));
    }

    public static TableResponse getTable(UUID accessTokenId, UUID listItemId) {
        Response response = getTableResponse(accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(TableResponse.class);
    }

    public static Response getTableResponse(UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_TABLE, "listItemId", listItemId));
    }

    public static void updateChecklistTableRowStatus(UUID accessTokenId, UUID rowId, Boolean status) {
        Response response = getUpdateChecklistTableRowStatusResponse(accessTokenId, rowId, status);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistTableRowStatusResponse(UUID accessTokenId, UUID rowId, Boolean status) {
        Map<String, Object> pathVariables = Map.of("rowId", rowId);

        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_TABLE_SET_ROW_STATUS, pathVariables));
    }

    public static void editCheckboxStatus(UUID accessTokenId, UUID columnId, Boolean status) {
        Response response = getEditCheckboxStatusResponse(accessTokenId, columnId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditCheckboxStatusResponse(UUID accessTokenId, UUID columnId, Boolean status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_TABLE_SET_CHECKBOX_COLUMN_STATUS, "columnId", columnId));
    }
}
