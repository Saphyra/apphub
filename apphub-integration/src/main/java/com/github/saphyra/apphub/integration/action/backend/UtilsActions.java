package com.github.saphyra.apphub.integration.action.backend;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.utils.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.integration.structure.api.utils.SetLogParameterVisibilityRequest;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsActions {
    public static List<LogParameterVisibilityResponse> getVisibilities(UUID accessTokenId, List<String> parameters) {
        Response response = getVisibilitiesResponse(accessTokenId, parameters);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(LogParameterVisibilityResponse[].class))
            .collect(Collectors.toList());
    }

    public static Response getVisibilitiesResponse(UUID accessTokenId, List<String> parameters) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(parameters)
            .put(UrlFactory.create(Endpoints.UTILS_LOG_FORMATTER_GET_VISIBILITY));
    }

    public static void setVisibility(UUID accessTokenId, SetLogParameterVisibilityRequest request) {
        Response response = getSetVisibilityResponse(accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getSetVisibilityResponse(UUID accessTokenId, SetLogParameterVisibilityRequest request) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.UTILS_LOG_FORMATTER_SET_VISIBILITY));
    }
}
