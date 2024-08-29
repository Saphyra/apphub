package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetQueueActions {
    public static void setPriority(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId, int priority) {
        Response response = getSetPriorityResponse(serverPort, accessTokenId, planetId, type, itemId, priority);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getSetPriorityResponse(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId, int priority) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(priority))
            .post(UrlFactory.create(
                serverPort,
                Endpoints.SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY,
                CollectionUtils.toMap(
                    new BiWrapper<>("planetId", planetId),
                    new BiWrapper<>("type", type),
                    new BiWrapper<>("itemId", itemId)
                )
            ));
    }

    public static void cancelItem(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        Response response = getCancelItemResponse(serverPort, accessTokenId, planetId, type, itemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelItemResponse(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(
                serverPort,
                Endpoints.SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM,
                CollectionUtils.toMap(
                    new BiWrapper<>("planetId", planetId),
                    new BiWrapper<>("type", type),
                    new BiWrapper<>("itemId", itemId)
                )
            ));
    }
}
