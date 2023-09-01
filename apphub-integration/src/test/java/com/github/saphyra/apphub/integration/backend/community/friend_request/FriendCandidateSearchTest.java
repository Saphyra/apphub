package com.github.saphyra.apphub.integration.backend.community.friend_request;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
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
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        UUID testUserAccessTokenId = IndexPageActions.registerAndLogin(language, testUserData);
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUserData.getEmail());

        search(language, accessTokenId, testUserData, testUserId);
        FriendRequestResponse friendRequestResponse = friendRequestAlreadySent(language, accessTokenId, testUserId);
        friendshipAlreadyExists(language, accessTokenId, testUserAccessTokenId, friendRequestResponse);
        blacklisted(language, accessTokenId, testUserId);
    }

    private static void search(Language language, UUID accessTokenId, RegistrationParameters testUserData, UUID testUserId) {
        List<SearchResultItem> searchResult = FriendRequestActions.search(language, accessTokenId, getEmailDomain());

        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getUserId()).isEqualTo(testUserId);
        assertThat(searchResult.get(0).getUsername()).isEqualTo(testUserData.getUsername());
        assertThat(searchResult.get(0).getEmail()).isEqualTo(testUserData.getEmail());
    }

    private static FriendRequestResponse friendRequestAlreadySent(Language language, UUID accessTokenId, UUID testUserId) {
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, testUserId);

        assertThat(FriendRequestActions.search(language, accessTokenId, getEmailDomain())).isEmpty();
        return friendRequestResponse;
    }

    private static void friendshipAlreadyExists(Language language, UUID accessTokenId, UUID testUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendRequestActions.acceptFriendRequest(language, testUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.search(language, accessTokenId, getEmailDomain())).isEmpty();
    }

    private static void blacklisted(Language language, UUID accessTokenId, UUID testUserId) {
        BlacklistActions.createBlacklist(language, accessTokenId, testUserId);

        assertThat(FriendRequestActions.search(language, accessTokenId, getEmailDomain())).isEmpty();
    }
}
