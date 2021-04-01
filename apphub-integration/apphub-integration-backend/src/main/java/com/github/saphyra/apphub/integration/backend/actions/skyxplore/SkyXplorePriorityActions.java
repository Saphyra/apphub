package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.PriorityType;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.StringIntMap;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
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
