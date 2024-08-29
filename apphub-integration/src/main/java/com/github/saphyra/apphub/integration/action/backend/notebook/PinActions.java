package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.PinGroupResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PinActions {
    public static Response getPinResponse(int serverPort, UUID accessTokenId, UUID listItemId, Boolean pinned) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinned))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_PIN_LIST_ITEM, "listItemId", listItemId));
    }

    public static void pin(int serverPort, UUID accessTokenId, UUID listItemId, Boolean pinned) {
        Response response = getPinResponse(serverPort, accessTokenId, listItemId, pinned);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<NotebookView> getPinnedItems(int serverPort, UUID accessTokenId) {
        Response response = getPinnedItemsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(NotebookView[].class))
            .collect(Collectors.toList());
    }

    public static Response getPinnedItemsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_PINNED_ITEMS));
    }

    public static List<NotebookView> getPinnedItems(int serverPort, UUID accessTokenId, UUID pinGroupId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_PINNED_ITEMS, Collections.emptyMap(), Map.of("pinGroupId", pinGroupId)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getCreatePinGroupResponse(int serverPort, UUID accessTokenId, String pinGroupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinGroupName))
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_CREATE_PIN_GROUP));
    }

    public static List<PinGroupResponse> createPinGroup(int serverPort, UUID accessTokenId, String pinGroupName) {
        Response response = getCreatePinGroupResponse(serverPort, accessTokenId, pinGroupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static Response getRenamePinGroupResponse(int serverPort, UUID accessTokenId, UUID pinGroupId, String pinGroupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinGroupName))
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_RENAME_PIN_GROUP, "pinGroupId", pinGroupId));
    }

    public static List<PinGroupResponse> renamePinGroup(int serverPort, UUID accessTokenId, UUID pinGroupId, String newPinGroupName) {
        Response response = getRenamePinGroupResponse(serverPort, accessTokenId, pinGroupId, newPinGroupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static List<NotebookView> addItemToPinGroup(int serverPort, UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        Response response = getAddItemToPinGroupResponse(serverPort, accessTokenId, pinGroupId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getAddItemToPinGroupResponse(int serverPort, UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_ADD_ITEM_TO_PIN_GROUP, Map.of("pinGroupId", pinGroupId, "listItemId", listItemId)));
    }

    public static List<NotebookView> removeItemFromPinGroup(int serverPort, UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        Response response = getReniveItemFromPinGroupResponse(serverPort, accessTokenId, pinGroupId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getReniveItemFromPinGroupResponse(int serverPort, UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP, Map.of("pinGroupId", pinGroupId, "listItemId", listItemId)));
    }

    public static List<PinGroupResponse> deletePinGroup(int serverPort, UUID accessTokenId, UUID pinGroupId) {
        Response response = getDeletePinGroupResponse(serverPort, accessTokenId, pinGroupId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static Response getDeletePinGroupResponse(int serverPort, UUID accessTokenId, UUID pinGroupId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_DELETE_PIN_GROUP, "pinGroupId", pinGroupId));
    }

    public static Response getPinGroupsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_GET_PIN_GROUPS));
    }

    public static Response getPinGroupOpenedResponse(int serverPort, UUID accessTokenId, UUID pinGroupId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .put(UrlFactory.create(serverPort, Endpoints.NOTEBOOK_PIN_GROUP_OPENED, "pinGroupId", pinGroupId));
    }
}
