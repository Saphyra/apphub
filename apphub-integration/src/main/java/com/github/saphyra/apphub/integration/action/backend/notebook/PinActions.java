package com.github.saphyra.apphub.integration.action.backend.notebook;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
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
    public static Response getPinResponse(int serverPort, String accessToken, UUID listItemId, Boolean pinned) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(pinned))
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_PIN_LIST_ITEM, "listItemId", listItemId));
    }

    public static void pin(int serverPort, String accessToken, UUID listItemId, Boolean pinned) {
        Response response = getPinResponse(serverPort, accessToken, listItemId, pinned);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<NotebookView> getPinnedItems(int serverPort, String accessToken) {
        Response response = getPinnedItemsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(NotebookView[].class))
            .collect(Collectors.toList());
    }

    public static Response getPinnedItemsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_PINNED_ITEMS));
    }

    public static List<NotebookView> getPinnedItems(int serverPort, String accessToken, UUID pinGroupId) {
        Response response = RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_PINNED_ITEMS, Collections.emptyMap(), Map.of("pinGroupId", pinGroupId)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getCreatePinGroupResponse(int serverPort, String accessToken, String pinGroupName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(pinGroupName))
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_CREATE_PIN_GROUP));
    }

    public static List<PinGroupResponse> createPinGroup(int serverPort, String accessToken, String pinGroupName) {
        Response response = getCreatePinGroupResponse(serverPort, accessToken, pinGroupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static Response getRenamePinGroupResponse(int serverPort, String accessToken, UUID pinGroupId, String pinGroupName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(pinGroupName))
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_RENAME_PIN_GROUP, "pinGroupId", pinGroupId));
    }

    public static List<PinGroupResponse> renamePinGroup(int serverPort, String accessToken, UUID pinGroupId, String newPinGroupName) {
        Response response = getRenamePinGroupResponse(serverPort, accessToken, pinGroupId, newPinGroupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static List<NotebookView> addItemToPinGroup(int serverPort, String accessToken, UUID pinGroupId, UUID listItemId) {
        Response response = getAddItemToPinGroupResponse(serverPort, accessToken, pinGroupId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getAddItemToPinGroupResponse(int serverPort, String accessToken, UUID pinGroupId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .post(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_ADD_ITEM_TO_PIN_GROUP, Map.of("pinGroupId", pinGroupId, "listItemId", listItemId)));
    }

    public static List<NotebookView> removeItemFromPinGroup(int serverPort, String accessToken, UUID pinGroupId, UUID listItemId) {
        Response response = getReniveItemFromPinGroupResponse(serverPort, accessToken, pinGroupId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getReniveItemFromPinGroupResponse(int serverPort, String accessToken, UUID pinGroupId, UUID listItemId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP, Map.of("pinGroupId", pinGroupId, "listItemId", listItemId)));
    }

    public static List<PinGroupResponse> deletePinGroup(int serverPort, String accessToken, UUID pinGroupId) {
        Response response = getDeletePinGroupResponse(serverPort, accessToken, pinGroupId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static Response getDeletePinGroupResponse(int serverPort, String accessToken, UUID pinGroupId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_DELETE_PIN_GROUP, "pinGroupId", pinGroupId));
    }

    public static Response getPinGroupsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_GET_PIN_GROUPS));
    }

    public static Response getPinGroupOpenedResponse(int serverPort, String accessToken, UUID pinGroupId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .put(UrlFactory.create(serverPort, NotebookEndpoints.NOTEBOOK_PIN_GROUP_OPENED, "pinGroupId", pinGroupId));
    }
}
