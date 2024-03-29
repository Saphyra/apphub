package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.community.BlacklistResponse;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistActions {
    public static List<SearchResultItem> search(UUID accessTokenId, String query) {
        Response response = getSearchResponse(accessTokenId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(UUID accessTokenId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(Endpoints.COMMUNITY_BLACKLIST_SEARCH));
    }

    public static BlacklistResponse createBlacklist(UUID accessTokenId, UUID blockedUserId) {
        Response response = getCreateResponse(accessTokenId, blockedUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BlacklistResponse.class);
    }

    public static Response getCreateResponse(UUID accessTokenId, UUID blockedUserId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(blockedUserId))
            .put(UrlFactory.create(Endpoints.COMMUNITY_CREATE_BLACKLIST));
    }

    public static List<BlacklistResponse> getBlacklists(UUID accessTokenId) {
        Response response = getBlacklistsResponse(accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BlacklistResponse[].class));
    }

    public static Response getBlacklistsResponse(UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(Endpoints.COMMUNITY_GET_BLACKLIST));
    }

    public static void deleteBlacklist(UUID accessTokenId, UUID blacklistId) {
        Response response = getDeleteBlacklistResponse(accessTokenId, blacklistId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteBlacklistResponse(UUID accessTokenId, UUID blacklistId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(Endpoints.COMMUNITY_DELETE_BLACKLIST, "blacklistId", blacklistId));
    }
}
