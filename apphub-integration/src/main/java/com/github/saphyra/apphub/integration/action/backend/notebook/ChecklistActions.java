package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.EditChecklistRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistActions {
    public static UUID createChecklist(int serverPort, UUID accessTokenId, CreateChecklistRequest request) {
        Response response = getCreateChecklistItemResponse(serverPort, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().jsonPath().getUUID("value");
    }

    public static Response getCreateChecklistItemResponse(int serverPort, UUID accessTokenId, CreateChecklistRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_CHECKLIST_ITEM));
    }

    public static ChecklistResponse getChecklist(int serverPort, UUID accessTokenId, UUID listItemId) {
        Response response = getChecklistResponse(serverPort, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        return response.getBody().as(ChecklistResponse.class);
    }

    public static Response getChecklistResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_CHECKLIST_ITEM, "listItemId", listItemId));
    }

    public static void editChecklist(int serverPort, UUID accessTokenId, EditChecklistRequest editRequest, UUID listItemId) {
        Response response = getEditChecklistResponse(serverPort, accessTokenId, editRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistResponse(int serverPort, UUID accessTokenId, EditChecklistRequest editRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_CHECKLIST_ITEM, "listItemId", listItemId));
    }

    public static void updateChecklistItemStatus(int serverPort, UUID accessTokenId, UUID checklistItemId, boolean status) {
        Response response = getUpdateChecklistItemStatusResponse(serverPort, accessTokenId, checklistItemId, status);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getUpdateChecklistItemStatusResponse(int serverPort, UUID accessTokenId, UUID checklistItemId, boolean status) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(status))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_STATUS, "checklistItemId", checklistItemId));
    }

    public static Response getDeleteCheckedChecklistItemsResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_DELETE_CHECKED_ITEMS_FROM_CHECKLIST, "listItemId", listItemId));
    }

    public static Response getOrderItemsResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_ORDER_CHECKLIST_ITEMS, "listItemId", listItemId));
    }

    public static Response getDeleteChecklistItemResponse(int serverPort, UUID accessTokenId, UUID checklistItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_DELETE_CHECKLIST_ITEM, "checklistItemId", checklistItemId));
    }

    public static void editChecklistItem(int serverPort, UUID accessTokenId, UUID checklistItemId, String content) {
        Response response = getEditChecklistItemResponse(serverPort, accessTokenId, checklistItemId, content);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditChecklistItemResponse(int serverPort, UUID accessTokenId, UUID checklistItemId, String content) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(content))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_UPDATE_CHECKLIST_ITEM_CONTENT, "checklistItemId", checklistItemId));
    }

    public static void addChecklistItem(int serverPort, UUID accessTokenId, UUID listItemId, AddChecklistItemRequest request) {
        Response response = getAddChecklistItemResponse(serverPort, accessTokenId, listItemId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getAddChecklistItemResponse(int serverPort, UUID accessTokenId, UUID listItemId, AddChecklistItemRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_ADD_CHECKLIST_ITEM, "listItemId", listItemId));
    }
}
