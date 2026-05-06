package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PriorityType;
import io.restassured.response.Response;

import java.util.Map;
import java.util.UUID;

public class SkyXplorePriorityActions {
    public static Map<String, Integer> getPriorities(int serverPort, String accessToken, UUID planetId) {
        return SkyXplorePlanetActions.getPlanetOverview(serverPort, accessToken, planetId)
            .getPriorities();
    }

    public static Response getUpdatePriorityResponse(int serverPort, String accessToken, UUID planetId, PriorityType priorityType, int newPriority) {
        return getUpdatePriorityResponse(serverPort, accessToken, planetId, priorityType.name(), newPriority);
    }

    public static Response getUpdatePriorityResponse(int serverPort, String accessToken, UUID planetId, String priorityType, int newPriority) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(newPriority))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_UPDATE_PRIORITY, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("priorityType", priorityType))));
    }
}
