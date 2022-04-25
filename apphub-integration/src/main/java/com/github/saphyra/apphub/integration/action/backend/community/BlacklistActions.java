package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.community.BlacklistResponse;
import com.github.saphyra.apphub.integration.structure.community.SearchResultItem;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistActions {
    public static List<SearchResultItem> search(Language language, UUID accessTokenId, String query) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.COMMUNITY_BLACKLIST_SEARCH));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static BlacklistResponse createBlacklist(Language language, UUID accessTokenId, UUID blockedUserId) {
        Response response = getCreateResponse(language, accessTokenId, blockedUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BlacklistResponse.class);
    }

    public static Response getCreateResponse(Language language, UUID accessTokenId, UUID blockedUserId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .body(new OneParamRequest<>(blockedUserId))
            .put(UrlFactory.create(Endpoints.COMMUNITY_CREATE_BLACKLIST));
    }

    public static List<BlacklistResponse> getBlacklists(Language language, UUID accessTokenId) {
        Response response = RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_BLACKLIST));

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BlacklistResponse[].class));
    }

    public static void deleteBlacklist(Language language, UUID accessTokenId, UUID blacklistId) {
        Response response = getDeleteBlacklistResponse(language, accessTokenId, blacklistId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteBlacklistResponse(Language language, UUID accessTokenId, UUID blacklistId) {
        return RequestFactory.createAuthorizedRequest(language, accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_DELETE_BLACKLIST, "blacklistId", blacklistId));
    }
}
