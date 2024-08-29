package com.github.saphyra.apphub.integration.backend.community.friend_request;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        UUID testUserAccessTokenId = IndexPageActions.registerAndLogin(getServerPort(), testUserData);
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUserData.getEmail());

        search(accessTokenId, testUserData, testUserId);
        FriendRequestResponse friendRequestResponse = friendRequestAlreadySent(accessTokenId, testUserId);
        friendshipAlreadyExists(accessTokenId, testUserAccessTokenId, friendRequestResponse);
        blacklisted(accessTokenId, testUserId);
    }

    private static void search(UUID accessTokenId, RegistrationParameters testUserData, UUID testUserId) {
        List<SearchResultItem> searchResult = FriendRequestActions.search(getServerPort(), accessTokenId, getEmailDomain());

        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getUserId()).isEqualTo(testUserId);
        assertThat(searchResult.get(0).getUsername()).isEqualTo(testUserData.getUsername());
        assertThat(searchResult.get(0).getEmail()).isEqualTo(testUserData.getEmail());
    }

    private static FriendRequestResponse friendRequestAlreadySent(UUID accessTokenId, UUID testUserId) {
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessTokenId, testUserId);

        assertThat(FriendRequestActions.search(getServerPort(), accessTokenId, getEmailDomain())).isEmpty();
        return friendRequestResponse;
    }

    private static void friendshipAlreadyExists(UUID accessTokenId, UUID testUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendRequestActions.acceptFriendRequest(getServerPort(), testUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.search(getServerPort(), accessTokenId, getEmailDomain())).isEmpty();
    }

    private static void blacklisted(UUID accessTokenId, UUID testUserId) {
        BlacklistActions.createBlacklist(getServerPort(), accessTokenId, testUserId);

        assertThat(FriendRequestActions.search(getServerPort(), accessTokenId, getEmailDomain())).isEmpty();
    }
}
