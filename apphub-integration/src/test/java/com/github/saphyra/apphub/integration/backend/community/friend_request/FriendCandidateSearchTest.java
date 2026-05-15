package com.github.saphyra.apphub.integration.backend.community.friend_request;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.community.SearchResultItem;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendCandidateSearchTest extends BackEndTest {
    @Test(groups = {"be", "community"})
    public void searchFriendCandidates() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        String testUserAccessTokenId = IndexPageActions.registerAndLogin(getServerPort(), testUserData);
        UUID testUserId = DynamoDbUtil.getUserIdByEmail(testUserData.getEmail());

        search(accessToken, testUserData, testUserId);
        FriendRequestResponse friendRequestResponse = friendRequestAlreadySent(accessToken, testUserId);
        friendshipAlreadyExists(accessToken, testUserAccessTokenId, friendRequestResponse);
        blacklisted(accessToken, testUserId);
    }

    private static void search(String accessToken, RegistrationParameters testUserData, UUID testUserId) {
        List<SearchResultItem> searchResult = FriendRequestActions.search(getServerPort(), accessToken, testUserData.getUsername());

        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.getFirst().getUserId()).isEqualTo(testUserId);
        assertThat(searchResult.getFirst().getUsername()).isEqualTo(testUserData.getUsername());
        assertThat(searchResult.getFirst().getEmail()).isEqualTo(testUserData.getEmail());
    }

    private static FriendRequestResponse friendRequestAlreadySent(String accessToken, UUID testUserId) {
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessToken, testUserId);

        assertThat(FriendRequestActions.search(getServerPort(), accessToken, getTestMethodName())).isEmpty();
        return friendRequestResponse;
    }

    private static void friendshipAlreadyExists(String accessToken, String testUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendRequestActions.acceptFriendRequest(getServerPort(), testUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.search(getServerPort(), accessToken, getTestMethodName())).isEmpty();
    }

    private static void blacklisted(String accessToken, UUID testUserId) {
        BlacklistActions.createBlacklist(getServerPort(), accessToken, testUserId);

        assertThat(FriendRequestActions.search(getServerPort(), accessToken, getTestMethodName())).isEmpty();
    }
}
