package com.github.saphyra.apphub.integration.backend.actions;

import com.github.saphyra.apphub.integration.backend.model.utils.LogParameterVisibilityResponse;
import com.github.saphyra.apphub.integration.backend.model.utils.SetLogParameterVisibilityRequest;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.RequestFactory;
import com.github.saphyra.apphub.integration.common.framework.UrlFactory;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsActions {
    public static List<LogParameterVisibilityResponse> getVisibilities(Language language, UUID accessTokenId, List<String> parameters) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(parameters)
            .put(UrlFactory.create(Endpoints.UTILS_LOG_FORMATTER_GET_VISIBILITY));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.stream(response.getBody().as(LogParameterVisibilityResponse[].class))
            .collect(Collectors.toList());
    }

    public static void setVisibility(Language language, UUID accessTokenId, SetLogParameterVisibilityRequest request) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(request)
            .post(UrlFactory.create(Endpoints.UTILS_LOG_FORMATTER_SET_VISIBILITY));

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
