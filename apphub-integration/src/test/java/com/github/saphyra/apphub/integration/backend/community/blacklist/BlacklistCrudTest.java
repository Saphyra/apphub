package com.github.saphyra.apphub.integration.backend.community.blacklist;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        String blockedUserAccessTokenId = IndexPageActions.registerAndLogin(getServerPort(), blockedUserData);
        UUID blockedUserId = DynamoDbUtil.getUserIdByEmail(blockedUserData.getEmail());

        create_userDoesNotExist(accessToken);
        BlacklistResponse blacklistResponse = create(accessToken, blockedUserData, blockedUserId);
        create_alreadyBlocked(accessToken, blockedUserId);
        UUID blacklistId = query(accessToken, blockedUserData, blockedUserId, blacklistResponse);
        delete_notFound(accessToken);
        delete_forbiddenOperation(blockedUserAccessTokenId, blacklistId);
        delete(accessToken, blacklistId);
    }

    private static void create_userDoesNotExist(String accessToken) {
        Response create_userDoesNotExistResponse = BlacklistActions.getCreateResponse(getServerPort(), accessToken, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(create_userDoesNotExistResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static BlacklistResponse create(String accessToken, RegistrationParameters blockedUserData, UUID blockedUserId) {
        BlacklistResponse blacklistResponse = BlacklistActions.createBlacklist(getServerPort(), accessToken, blockedUserId);
        assertThat(blacklistResponse.getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(blacklistResponse.getUsername()).isEqualTo(blockedUserData.getUsername());
        assertThat(blacklistResponse.getEmail()).isEqualTo(blockedUserData.getEmail());
        return blacklistResponse;
    }

    private static void create_alreadyBlocked(String accessToken, UUID blockedUserId) {
        Response create_alreadyBlockedResponse = BlacklistActions.getCreateResponse(getServerPort(), accessToken, blockedUserId);

        ResponseValidator.verifyErrorResponse(create_alreadyBlockedResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static UUID query(String accessToken, RegistrationParameters blockedUserData, UUID blockedUserId, BlacklistResponse blacklistResponse) {
        List<BlacklistResponse> blacklists = BlacklistActions.getBlacklists(getServerPort(), accessToken);

        assertThat(blacklists).hasSize(1);
        UUID blacklistId = blacklists.getFirst().getBlacklistId();
        assertThat(blacklistId).isEqualTo(blacklistResponse.getBlacklistId());
        assertThat(blacklists.getFirst().getBlockedUserId()).isEqualTo(blockedUserId);
        assertThat(blacklists.getFirst().getUsername()).isEqualTo(blockedUserData.getUsername());
        assertThat(blacklists.getFirst().getEmail()).isEqualTo(blockedUserData.getEmail());
        return blacklistId;
    }

    private static void delete_notFound(String accessToken) {
        Response delete_notFoundResponse = BlacklistActions.getDeleteBlacklistResponse(getServerPort(), accessToken, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(String blockedUserAccessTokenId, UUID blacklistId) {
        Response delete_forbiddenOperationResponse = BlacklistActions.getDeleteBlacklistResponse(getServerPort(), blockedUserAccessTokenId, blacklistId);

        ResponseValidator.verifyForbiddenOperation(delete_forbiddenOperationResponse);
    }

    private static void delete(String accessToken, UUID blacklistId) {
        BlacklistActions.deleteBlacklist(getServerPort(), accessToken, blacklistId);

        assertThat(BlacklistActions.getBlacklists(getServerPort(), accessToken)).isEmpty();
    }

    @Test(groups = {"be", "community"})
    public void createBlacklistShouldRemoveFriendship() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), blockedUserData);
        UUID blockedUserId = DynamoDbUtil.getUserIdByEmail(blockedUserData.getEmail());

        FriendRequestActions.createFriendRequest(getServerPort(), accessToken, blockedUserId);

        BlacklistActions.createBlacklist(getServerPort(), accessToken, blockedUserId);

        assertThat(FriendRequestActions.getSentFriendRequests(getServerPort(), accessToken)).isEmpty();
    }

    @Test(groups = {"be", "community"})
    public void createBlacklistShouldRemoveFriendRequest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        String blockedUserAccessTokenId = IndexPageActions.registerAndLogin(getServerPort(), blockedUserData);
        UUID blockedUserId = DynamoDbUtil.getUserIdByEmail(blockedUserData.getEmail());

        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessToken, blockedUserId);
        FriendRequestActions.acceptFriendRequest(getServerPort(), blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        BlacklistActions.createBlacklist(getServerPort(), accessToken, blockedUserId);

        assertThat(FriendshipActions.getFriendships(getServerPort(), accessToken)).isEmpty();
    }
}
