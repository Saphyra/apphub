package com.github.saphyra.apphub.integration.action.backend.diary;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.diary.EventSearchResponse;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DiarySearchActions {
    public static List<EventSearchResponse> search(Language language, UUID accessTokenId, String query) {
        Response response = getSearchResponse(language, accessTokenId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(EventSearchResponse[].class));
    }

    public static Response getSearchResponse(Language language, UUID accessTokenId, String query) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.DIARY_SEARCH));
    }
}
