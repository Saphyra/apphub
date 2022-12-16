package com.github.saphyra.apphub.integration.action.backend.skyxplore;

import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.skyxplore.CitizenResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXplorePopulationActions {
    public static List<CitizenResponse> getPopulation(Language language, UUID accessTokenId, UUID planetId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_GET_POPULATION, "planetId", planetId));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(CitizenResponse[].class))
            .collect(Collectors.toList());
    }

    public static CitizenResponse renameCitizen(Language language, UUID accessTokenId, UUID planetId, UUID citizenId, String newName) {
        Response response = getRenameCitizenResponse(language, accessTokenId, planetId, citizenId, newName);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(CitizenResponse.class);
    }

    public static Response getRenameCitizenResponse(Language language, UUID accessTokenId, UUID planetId, UUID citizenId, String newName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(newName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("citizenId", citizenId))));
    }
}
