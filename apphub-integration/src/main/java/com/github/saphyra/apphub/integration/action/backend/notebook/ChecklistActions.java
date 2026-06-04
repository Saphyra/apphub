package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.EditChecklistRequest;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistActions {
    public static UUID createChecklist(int serverPort, String accessToken, CreateChecklistRequest request) {
        Response response = getCreateChecklistItemResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateChecklistItemResponse(int serverPort, String accessToken, CreateChecklistRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_CHECKLIST));
    }

    public static ChecklistResponse getChecklist(int serverPort, String accessToken, UUID listItemId) {
        Response response = getChecklistResponse(serverPort, accessToken, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChecklistResponse.class);
    }

    public static Response getChecklistResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_CHECKLIST, "listItemId", listItemId));
    }

    public static void editChecklist(int serverPort, String accessToken, EditChecklistRequest editRequest, UUID listItemId) {
        Response response = getEditChecklistResponse(serverPort, accessToken, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistResponse(int serverPort, String accessToken, EditChecklistRequest editRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(editRequest)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_EDIT_CHECKLIST, "listItemId", listItemId));
    }

    public static void updateChecklistItemStatus(int serverPort, String accessToken, UUID listItemId, UUID checklistItemId, boolean status) {
        Response response = getUpdateChecklistItemStatusResponse(serverPort, accessToken, listItemId, checklistItemId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistItemStatusResponse(int serverPort, String accessToken, UUID listItemId, UUID checklistItemId, boolean status) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(
                serverPort,
                NotebookEndpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS,
                Map.of(
                    "listItemId", listItemId,
                    "checklistItemId", checklistItemId
                )
            ));
    }

    public static Response getDeleteCheckedChecklistItemsResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CHECKLIST_DELETE_CHECKED, "listItemId", listItemId));
    }

    public static Response getOrderItemsResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS, "listItemId", listItemId));
    }

    public static Response getDeleteChecklistItemResponse(int serverPort, String accessToken, UUID listItemId, UUID checklistItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(
                serverPort,
                NotebookEndpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM,
                Map.of(
                    "listItemId", listItemId,
                    "checklistItemId", checklistItemId
                )
            ));
    }

    public static void editChecklistItem(int serverPort, String accessToken, UUID listItemId, UUID checklistItemId, String content) {
        Response response = getEditChecklistItemResponse(serverPort, accessToken, listItemId, checklistItemId, content);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistItemResponse(int serverPort, String accessToken, UUID listItemId, UUID checklistItemId, String content) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(content))
            .post(UrlFactory.create(
                serverPort,
                NotebookEndpoints.NOTEBOOK_EDIT_CHECKLIST_ITEM,
                Map.of(
                    "listItemId", listItemId,
                    "checklistItemId", checklistItemId
                )
            ));
    }

    public static void addChecklistItem(int serverPort, String accessToken, UUID listItemId, AddChecklistItemRequest request) {
        Response response = getAddChecklistItemResponse(serverPort, accessToken, listItemId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAddChecklistItemResponse(int serverPort, String accessToken, UUID listItemId, AddChecklistItemRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_ADD_CHECKLIST_ITEM, "listItemId", listItemId));
    }
}
