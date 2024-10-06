package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CommunityEndpoints;
import com.github.saphyra.apphub.integration.structure.api.OneParamRequest;
import com.github.saphyra.apphub.integration.structure.api.community.BlacklistResponse;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistActions {
    public static List<SearchResultItem> search(int serverPort, UUID accessTokenId, String query) {
        Response response = getSearchResponse(serverPort, accessTokenId, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(int serverPort, UUID accessTokenId, String query) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_BLACKLIST_SEARCH));
    }

    public static BlacklistResponse createBlacklist(int serverPort, UUID accessTokenId, UUID blockedUserId) {
        Response response = getCreateResponse(serverPort, accessTokenId, blockedUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BlacklistResponse.class);
    }

    public static Response getCreateResponse(int serverPort, UUID accessTokenId, UUID blockedUserId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .body(new OneParamRequest<>(blockedUserId))
            .put(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_CREATE_BLACKLIST));
    }

    public static List<BlacklistResponse> getBlacklists(int serverPort, UUID accessTokenId) {
        Response response = getBlacklistsResponse(serverPort, accessTokenId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BlacklistResponse[].class));
    }

    public static Response getBlacklistsResponse(int serverPort, UUID accessTokenId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_BLACKLIST));
    }

    public static void deleteBlacklist(int serverPort, UUID accessTokenId, UUID blacklistId) {
        Response response = getDeleteBlacklistResponse(serverPort, accessTokenId, blacklistId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteBlacklistResponse(int serverPort, UUID accessTokenId, UUID blacklistId) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_DELETE_BLACKLIST, "blacklistId", blacklistId));
    }
}
