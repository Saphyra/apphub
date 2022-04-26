package com.github.saphyra.apphub.integraton.backend.community.friend_request;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.community.SearchResultItem;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendCandidateSearchTest extends BackEndTest {
    @Test(groups = "community")
    public void searchFriendCandidates() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        UUID testUserAccessTokenId = IndexPageActions.registerAndLogin(language, testUserData);
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUserData.getEmail());

        List<SearchResultItem> searchResult = FriendRequestActions.search(language, accessTokenId, getEmailDomain());

        assertThat(searchResult).hasSize(1);
        assertThat(searchResult.get(0).getUserId()).isEqualTo(testUserId);
        assertThat(searchResult.get(0).getUsername()).isEqualTo(testUserData.getUsername());
        assertThat(searchResult.get(0).getEmail()).isEqualTo(testUserData.getEmail());

        //FriendRequest already sent
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, testUserId);

        assertThat(FriendRequestActions.search(language, accessTokenId, getEmailDomain())).isEmpty();

        //Friendship already exists
        FriendRequestActions.acceptFriendRequest(language, testUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.search(language, accessTokenId, getEmailDomain())).isEmpty();

        //Blacklisted
        BlacklistActions.createBlacklist(language, accessTokenId, testUserId);

        assertThat(FriendRequestActions.search(language, accessTokenId, getEmailDomain())).isEmpty();
    }
}
