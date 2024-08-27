package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ListItemActions {
    public static void deleteListItem(int serverPort, UUID accessTokenId, UUID listItemId) {
        Response response = getDeleteListItemResponse(serverPort, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteListItemResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_DELETE_LIST_ITEM, "listItemId", listItemId));
    }

    public static void editListItem(int serverPort, UUID accessTokenId, EditListItemRequest editListItemRequest, UUID listItemId) {
        Response response = getEditListItemResponse(serverPort, accessTokenId, editListItemRequest, listItemId);
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getEditListItemResponse(int serverPort, UUID accessTokenId, EditListItemRequest editListItemRequest, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(editListItemRequest)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_EDIT_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getCloneListItemResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CLONE_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getSearchResponse(int serverPort, UUID accessTokenId, String searchText) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(searchText))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_SEARCH));
    }

    public static List<NotebookView> search(int serverPort, UUID accessTokenId, String search) {
        Response response = getSearchResponse(serverPort, accessTokenId, search);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static void archive(int serverPort, UUID accessTokenId, UUID listItemId, boolean archived) {
        Response response = getArchiveResponse(serverPort, accessTokenId, listItemId, archived);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getArchiveResponse(int serverPort, UUID accessTokenId, UUID listItemId, Boolean archived) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(archived))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_ARCHIVE_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getFindListItemResponse(int serverPort, UUID accessTokenId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_LIST_ITEM, "listItemId", listItemId));
    }

    public static Response getMoveListItemResponse(int serverPort, UUID accessTokenId, UUID listItemId, UUID parent) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(parent))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_MOVE_LIST_ITEM, "listItemId", listItemId));
    }
}
