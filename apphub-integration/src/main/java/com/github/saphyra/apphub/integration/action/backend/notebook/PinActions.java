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
    public static Response getPinResponse(UUID accessTokenId, UUID listItemId, Boolean pinned) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinned))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_PIN_LIST_ITEM, "listItemId", listItemId));
    }

    public static void pin(UUID accessTokenId, UUID listItemId, Boolean pinned) {
        Response response = getPinResponse(accessTokenId, listItemId, pinned);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static List<NotebookView> getPinnedItems(UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_PINNED_ITEMS));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(NotebookView[].class))
            .collect(Collectors.toList());
    }

    public static List<NotebookView> getPinnedItems(UUID accessTokenId, UUID pinGroupId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.NOTEBOOK_GET_PINNED_ITEMS, Collections.emptyMap(), Map.of("pinGroupId", pinGroupId)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static Response getCreatePinGroupResponse(UUID accessTokenId, String pinGroupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinGroupName))
            .put(UrlFactory.create(Endpoints.NOTEBOOK_CREATE_PIN_GROUP));
    }

    public static List<PinGroupResponse> createPinGroup(UUID accessTokenId, String pinGroupName) {
        Response response = getCreatePinGroupResponse(accessTokenId, pinGroupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static Response getRenamePinGroupResponse(UUID accessTokenId, UUID pinGroupId, String pinGroupName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(pinGroupName))
            .post(UrlFactory.create(Endpoints.NOTEBOOK_RENAME_PIN_GROUP, "pinGroupId", pinGroupId));
    }

    public static List<PinGroupResponse> renamePinGroup(UUID accessTokenId, UUID pinGroupId, String newPinGroupName) {
        Response response = getRenamePinGroupResponse(accessTokenId, pinGroupId, newPinGroupName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }

    public static List<NotebookView> addItemToPinGroup(UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .post(UrlFactory.create(Endpoints.NOTEBOOK_ADD_ITEM_TO_PIN_GROUP, Map.of("pinGroupId", pinGroupId, "listItemId", listItemId)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static List<NotebookView> removeItemFromPinGroup(UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.NOTEBOOK_REMOVE_ITEM_FROM_PIN_GROUP, Map.of("pinGroupId", pinGroupId, "listItemId", listItemId)));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(NotebookView[].class));
    }

    public static List<PinGroupResponse> deletePinGroup(UUID accessTokenId, UUID pinGroupId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.NOTEBOOK_DELETE_PIN_GROUP, "pinGroupId", pinGroupId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(PinGroupResponse[].class));
    }
}
