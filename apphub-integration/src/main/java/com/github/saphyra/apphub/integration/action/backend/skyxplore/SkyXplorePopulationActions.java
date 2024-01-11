package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.CitizenResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePopulationActions {
    public static List<CitizenResponse> getPopulation(UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_POPULATION, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(CitizenResponse[].class))
            .collect(Collectors.toList());
    }

    public static CitizenResponse renameCitizen(UUID accessTokenId, UUID citizenId, String newName) {
        Response response = getRenameCitizenResponse(accessTokenId, citizenId, newName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CitizenResponse.class);
    }

    public static Response getRenameCitizenResponse(UUID accessTokenId, UUID citizenId, String newName) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(newName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, "citizenId", citizenId));
    }
}
