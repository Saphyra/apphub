package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.CitizenResponse;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
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

    public static Response getRenameCitizenResponse(Language language, UUID accessTokenId, UUID planetId, UUID citizenId, String newName) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(newName))
            .post(UrlFactory.create(Endpoints.SKYXPLORE_PLANET_RENAME_CITIZEN, CollectionUtils.toMap(new BiWrapper<>("planetId", planetId), new BiWrapper<>("citizenId", citizenId))));
    }
}
