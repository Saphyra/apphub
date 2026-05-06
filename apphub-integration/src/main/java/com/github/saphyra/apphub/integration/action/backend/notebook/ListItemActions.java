package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ListItemActions {
    public static void deleteListItem(int serverPort, String accessToken, UUID listItemId) {
        Response response = getDeleteListItemResponse(serverPort, accessToken, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteListItemResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_DELETE_LIST_ITEM, "listItemId", listItemId));
    }

    public static void editListItem(int serverPort, String accessToken, EditListItemRequest editListItemRequest, UUID listItemId) {
        Response response = getEditListItemResponse(serverPort, accessToken, editListItemRequest, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditListItemResponse(int serverPort, String accessToken, EditListItemRequest editListItemRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(editListItemRequest)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_EDIT_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getCloneListItemResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CLONE_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getSearchResponse(int serverPort, String accessToken, String searchText) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(searchText))
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_SEARCH));
    }

    public static List<NotebookView> search(int serverPort, String accessToken, String search) {
        Response response = getSearchResponse(serverPort, accessToken, search);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static void archive(int serverPort, String accessToken, UUID listItemId, boolean archived) {
        Response response = getArchiveResponse(serverPort, accessToken, listItemId, archived);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getArchiveResponse(int serverPort, String accessToken, UUID listItemId, Boolean archived) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(archived))
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_ARCHIVE_ITEM, "listItemId", listItemId));
    }

    public static Response getFindListItemResponse(int serverPort, String accessToken, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getMoveListItemResponse(int serverPort, String accessToken, UUID listItemId, UUID parent) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(parent))
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_MOVE_LIST_ITEM, "listItemId", listItemId));
    }
}
