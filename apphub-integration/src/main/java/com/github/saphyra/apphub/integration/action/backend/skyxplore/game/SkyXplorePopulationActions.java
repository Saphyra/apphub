package com.github.saphyra.apphub.integration.action.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.skyxplore.SkyXploreGameEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePopulationActions {
    public static List<CitizenResponse> getPopulation(int serverPort, String accessToken, UUID planetId) {
        Response response = getPopulationResponse(serverPort, accessToken, planetId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(CitizenResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getPopulationResponse(int serverPort, String accessToken, UUID planetId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_GET_POPULATION, "planetId", planetId));
    }

    public static void renameCitizen(int serverPort, String accessToken, UUID citizenId, String newName) {
        Response response = getRenameCitizenResponse(serverPort, accessToken, citizenId, newName);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getRenameCitizenResponse(int serverPort, String accessToken, UUID citizenId, String newName) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(newName))
            .post(UrlFactory.create(serverPort, SkyXploreGameEndpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, "citizenId", citizenId));
    }
}
