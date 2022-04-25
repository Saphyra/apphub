package com.github.saphyra.apphub.integraton.backend.community.blacklist;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.community.BlacklistResponse;
import com.github.saphyra.apphub.integration.structure.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistCrudTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = "community")
    public void blacklistCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        UUID blockedUserAccessTokenId = IndexPageActions.registerAndLogin(language, blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        //Create - User does not exist
        Response create_userDoesNotExistResponse = BlacklistActions.getCreateResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, create_userDoesNotExistResponse, 404, ErrorCode.USER_NOT_FOUND);

        //Create
        BlacklistResponse blacklistResponse = BlacklistActions.createBlacklist(language, accessTokenId, blockedUserId);
        assertThat(blacklistResponse.getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(blacklistResponse.getUsername()).isEqualTo(blockedUserData.getUsername());
        assertThat(blacklistResponse.getEmail()).isEqualTo(blockedUserData.getEmail());

        //Create - Already blocked
        Response create_alreadyBlockedResponse = BlacklistActions.getCreateResponse(language, accessTokenId, blockedUserId);

        ResponseValidator.verifyErrorResponse(language, create_alreadyBlockedResponse, 409, ErrorCode.ALREADY_EXISTS);

        //Query
        List<BlacklistResponse> blacklists = BlacklistActions.getBlacklists(language, accessTokenId);

        assertThat(blacklists).hasSize(1);
        UUID blacklistId = blacklists.get(0).getBlacklistId();
        assertThat(blacklistId).isEqualTo(blacklistResponse.getBlacklistId());
        assertThat(blacklists.get(0).getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(blacklists.get(0).getUsername()).isEqualTo(blockedUserData.getUsername());
        assertThat(blacklists.get(0).getEmail()).isEqualTo(blockedUserData.getEmail());

        //Delete - Not found
        Response delete_notFoundResponse = BlacklistActions.getDeleteBlacklistResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);

        //Delete - Forbidden operation
        Response delete_forbiddenOperationResponse = BlacklistActions.getDeleteBlacklistResponse(language, blockedUserAccessTokenId, blacklistId);

        ResponseValidator.verifyForbiddenOperation(language, delete_forbiddenOperationResponse);

        //Delete
        BlacklistActions.deleteBlacklist(language, accessTokenId, blacklistId);

        assertThat(BlacklistActions.getBlacklists(language, accessTokenId)).isEmpty();
    }

    @Test(groups = "community")
    public void createBlacklistShouldRemoveFriendship() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(language, blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        FriendRequestActions.createFriendRequest(language, accessTokenId, blockedUserId);

        BlacklistActions.createBlacklist(language, accessTokenId, blockedUserId);

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
    }

    @Test(groups = "community")
    public void createBlacklistShouldRemoveFriendRequest() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        UUID blockedUserAccessTokenId = IndexPageActions.registerAndLogin(language, blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, blockedUserId);
        FriendRequestActions.acceptFriendRequest(language, blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        BlacklistActions.createBlacklist(language, accessTokenId, blockedUserId);

        assertThat(FriendshipActions.getFriendships(language, accessTokenId)).isEmpty();
    }
}
