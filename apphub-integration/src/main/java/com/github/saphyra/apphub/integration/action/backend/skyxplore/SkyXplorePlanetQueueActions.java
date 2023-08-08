package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePlanetQueueActions {
    public static List<QueueResponse> getQueue(Language language, UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_QUEUE, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(QueueResponse[].class));
    }

    public static void setPriority(Language language, UUID accessTokenId, UUID planetId, String type, UUID itemId, int priority) {
        Response response = getSetPriorityResponse(language, accessTokenId, planetId, type, itemId, priority);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getSetPriorityResponse(Language language, UUID accessTokenId, UUID planetId, String type, UUID itemId, int priority) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(priority))
            .post(UrlFactory.create(
                Endpoints.SKYXPLORE_PLANET_SET_QUEUE_ITEM_PRIORITY,
                CollectionUtils.toMap(
                    new BiWrapper<>("planetId", planetId),
                    new BiWrapper<>("type", type),
                    new BiWrapper<>("itemId", itemId)
                )
            ));
    }

    public static void cancelItem(Language language, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        Response response = getCancelItemResponse(language, accessTokenId, planetId, type, itemId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getCancelItemResponse(Language language, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(
                Endpoints.SKYXPLORE_PLANET_CANCEL_QUEUE_ITEM,
                CollectionUtils.toMap(
                    new BiWrapper<>("planetId", planetId),
                    new BiWrapper<>("type", type),
                    new BiWrapper<>("itemId", itemId)
                )
            ));
    }
}
