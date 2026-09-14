package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.core.util.Nullable;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.ShareObjectRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarShareActions {
    public static Response getGetSharedObjectResponse(int serverPort, String accessToken, SharedObjectType type, UUID objectId, @Nullable UUID parent) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(
                serverPort,
                CalendarEndpoints.CALENDAR_GET_SHARED_ITEM,
                Map.of(
                    "type", type,
                    "id", objectId
                ),
                CollectionUtils.toMap(new BiWrapper<>("parent", parent))
            ));
    }

    public static Response getShareObjectResponse(int serverPort, String accessToken, ShareObjectRequest request) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(request)
            .put(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_SHARE_OBJECT));
    }

    public static Response getEditSharedObjectResponse(int serverPort, String accessToken, SharedObjectType type, UUID objectId, UUID sharedWith, Set<Grant> grants) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(grants)
            .post(UrlFactory.create(
                serverPort,
                CalendarEndpoints.CALENDAR_SHARE_EDIT_GRANTS,
                Map.of(
                    "type", type,
                    "id", objectId,
                    "sharedWith", sharedWith
                )
            ));
    }

    public static Response getUnshareResponse(int serverPort, String accessToken, SharedObjectType type, UUID objectId, UUID sharedWith) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(
                serverPort,
                CalendarEndpoints.CALENDAR_UNSHARE,
                Map.of(
                    "type", type,
                    "id", objectId,
                    "sharedWith", sharedWith
                )
            ));
    }

    public static SharedObjectResponse getSharedObject(int serverPort, String ownerToken, SharedObjectType sharedObjectType, UUID labelId, UUID parent) {
        Response response = getGetSharedObjectResponse(serverPort, ownerToken, sharedObjectType, labelId, parent);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.as(SharedObjectResponse.class);
    }

    public static void shareObject(int serverPort, String accessToken, ShareObjectRequest request) {
        Response response = getShareObjectResponse(serverPort, accessToken, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void editOperations(int serverPort, String accessToken, SharedObjectType type, UUID labelId, UUID sharedWith, Set<Grant> grants) {
        Response response = getEditSharedObjectResponse(serverPort, accessToken, type, labelId, sharedWith, grants);

        assertThat(response.getStatusCode()).isEqualTo(200);

    }

    public static void editSharedObject(int serverPort, String accessToken, SharedObjectType type, UUID objectId, UUID sharedWith, Set<Grant> grants) {
        Response response = getEditSharedObjectResponse(serverPort, accessToken, type, objectId, sharedWith, grants);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static void unshareObject(int serverPort, String accessToken, SharedObjectType type, UUID objectId, UUID sharedWith) {
        Response response = getUnshareResponse(serverPort, accessToken, type, objectId, sharedWith);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
