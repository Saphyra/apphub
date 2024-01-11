package com.github.saphyra.apphub.integration.backend.community.blacklist;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.community.BlacklistResponse;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistCrudTest extends BackEndTest {
    @Test(groups = {"be", "community"})
    public void blacklistCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        UUID blockedUserAccessTokenId = IndexPageActions.registerAndLogin(blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        create_userDoesNotExist(accessTokenId);
        BlacklistResponse blacklistResponse = create(accessTokenId, blockedUserData, blockedUserId);
        create_alreadyBlocked(accessTokenId, blockedUserId);
        UUID blacklistId = query(accessTokenId, blockedUserData, blockedUserId, blacklistResponse);
        delete_notFound(accessTokenId);
        delete_forbiddenOperation(blockedUserAccessTokenId, blacklistId);
        delete(accessTokenId, blacklistId);
    }

    private static void create_userDoesNotExist(UUID accessTokenId) {
        Response create_userDoesNotExistResponse = BlacklistActions.getCreateResponse(accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(create_userDoesNotExistResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static BlacklistResponse create(UUID accessTokenId, RegistrationParameters blockedUserData, UUID blockedUserId) {
        BlacklistResponse blacklistResponse = BlacklistActions.createBlacklist(accessTokenId, blockedUserId);
        assertThat(blacklistResponse.getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(blacklistResponse.getUsername()).isEqualTo(blockedUserData.getUsername());
        assertThat(blacklistResponse.getEmail()).isEqualTo(blockedUserData.getEmail());
        return blacklistResponse;
    }

    private static void create_alreadyBlocked(UUID accessTokenId, UUID blockedUserId) {
        Response create_alreadyBlockedResponse = BlacklistActions.getCreateResponse(accessTokenId, blockedUserId);

        ResponseValidator.verifyErrorResponse(create_alreadyBlockedResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static UUID query(UUID accessTokenId, RegistrationParameters blockedUserData, UUID blockedUserId, BlacklistResponse blacklistResponse) {
        List<BlacklistResponse> blacklists = BlacklistActions.getBlacklists(accessTokenId);

        assertThat(blacklists).hasSize(1);
        UUID blacklistId = blacklists.get(0).getBlacklistId();
        assertThat(blacklistId).isEqualTo(blacklistResponse.getBlacklistId());
        assertThat(blacklists.get(0).getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(blacklists.get(0).getUsername()).isEqualTo(blockedUserData.getUsername());
        assertThat(blacklists.get(0).getEmail()).isEqualTo(blockedUserData.getEmail());
        return blacklistId;
    }

    private static void delete_notFound(UUID accessTokenId) {
        Response delete_notFoundResponse = BlacklistActions.getDeleteBlacklistResponse(accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(UUID blockedUserAccessTokenId, UUID blacklistId) {
        Response delete_forbiddenOperationResponse = BlacklistActions.getDeleteBlacklistResponse(blockedUserAccessTokenId, blacklistId);

        ResponseValidator.verifyForbiddenOperation(delete_forbiddenOperationResponse);
    }

    private static void delete(UUID accessTokenId, UUID blacklistId) {
        BlacklistActions.deleteBlacklist(accessTokenId, blacklistId);

        assertThat(BlacklistActions.getBlacklists(accessTokenId)).isEmpty();
    }

    @Test(groups = {"be", "community"})
    public void createBlacklistShouldRemoveFriendship() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        FriendRequestActions.createFriendRequest(accessTokenId, blockedUserId);

        BlacklistActions.createBlacklist(accessTokenId, blockedUserId);

        assertThat(FriendRequestActions.getSentFriendRequests(accessTokenId)).isEmpty();
    }

    @Test(groups = {"be", "community"})
    public void createBlacklistShouldRemoveFriendRequest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        UUID blockedUserAccessTokenId = IndexPageActions.registerAndLogin(blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(accessTokenId, blockedUserId);
        FriendRequestActions.acceptFriendRequest(blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        BlacklistActions.createBlacklist(accessTokenId, blockedUserId);

        assertThat(FriendshipActions.getFriendships(accessTokenId)).isEmpty();
    }
}
