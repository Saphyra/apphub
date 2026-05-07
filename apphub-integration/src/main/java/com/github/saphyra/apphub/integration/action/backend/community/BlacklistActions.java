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
    public static List<SearchResultItem> search(int serverPort, String accessToken, String query) {
        Response response = getSearchResponse(serverPort, accessToken, query);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(SearchResultItem[].class));
    }

    public static Response getSearchResponse(int serverPort, String accessToken, String query) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(query))
            .post(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_BLACKLIST_SEARCH));
    }

    public static BlacklistResponse createBlacklist(int serverPort, String accessToken, UUID blockedUserId) {
        Response response = getCreateResponse(serverPort, accessToken, blockedUserId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response.getBody().as(BlacklistResponse.class);
    }

    public static Response getCreateResponse(int serverPort, String accessToken, UUID blockedUserId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .body(new OneParamRequest<>(blockedUserId))
            .put(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_CREATE_BLACKLIST));
    }

    public static List<BlacklistResponse> getBlacklists(int serverPort, String accessToken) {
        Response response = getBlacklistsResponse(serverPort, accessToken);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return Arrays.asList(response.getBody().as(BlacklistResponse[].class));
    }

    public static Response getBlacklistsResponse(int serverPort, String accessToken) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .get(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_GET_BLACKLIST));
    }

    public static void deleteBlacklist(int serverPort, String accessToken, UUID blacklistId) {
        Response response = getDeleteBlacklistResponse(serverPort, accessToken, blacklistId);

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    public static Response getDeleteBlacklistResponse(int serverPort, String accessToken, UUID blacklistId) {
        return RequestFactory.createAuthorizedRequest(accessToken)
            .delete(UrlFactory.create(serverPort, CommunityEndpoints.COMMUNITY_DELETE_BLACKLIST, "blacklistId", blacklistId));
    }
}
