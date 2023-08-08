package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.StringIntMap;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PriorityType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePriorityActions {
    public static Map<String, Integer> getPriorities(Language language, UUID accessTokenId, UUID planetId) {
        Response response = getPrioritiesResponse(language, accessTokenId, planetId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(StringIntMap.class);
    }

    public static Response getPrioritiesResponse(Language language, UUID accessTokenId, UUID planetId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_PRIORITIES, "planetId", planetId));
    }

    public static Response getUpdatePriorityResponse(Language language, UUID accessTokenId, UUID planetId, PriorityType priorityType, int newPriority) {
        return getUpdatePriorityResponse(language, accessTokenId, planetId, priorityType.name(), newPriority);
    }

    public static Response getUpdatePriorityResponse(Language language, UUID accessTokenId, UUID planetId, String priorityType, int newPriority) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(newPriority))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_UPDATE_PRIORITY, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("priorityType", priorityType))));
    }
}
