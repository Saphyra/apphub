package com.github.saphyra.apphub.integration.backend.community.friend_request;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.community.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendRequestCrudTest extends BackEndTest {
    @Test(groups = {"be", "community"})
    public void friendRequestCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        RegistrationParameters friendUserData = RegistrationParameters.validParameters();
        UUID friendUserAccessTokenId = IndexPageActions.registerAndLogin(friendUserData);
        UUID friendUserId = DatabaseUtil.getUserIdByEmail(friendUserData.getEmail());

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        UUID blockedUserAccessTokenId = IndexPageActions.registerAndLogin(blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        create_userNotFound(accessTokenId);
        create_blocked(accessTokenId, blockedUserId);
        FriendRequestResponse friendRequestResponse = create(accessTokenId, friendUserData, friendUserId);
        create_alreadyExists(accessTokenId, friendUserId);
        query_sent(accessTokenId, friendRequestResponse);
        query_received(userData, friendUserAccessTokenId, friendRequestResponse);
        delete_notFound(accessTokenId);
        delete_forbiddenOperation(blockedUserAccessTokenId, friendRequestResponse);
        deleteBySender(accessTokenId, friendUserAccessTokenId, friendRequestResponse);
        deleteByReceiver(accessTokenId, friendUserAccessTokenId, friendUserId);
        friendRequestResponse = accept_notFound(accessTokenId, friendUserAccessTokenId, friendUserId);
        accept_forbiddenOperation(blockedUserAccessTokenId, friendRequestResponse);
        accept(userData, accessTokenId, friendUserAccessTokenId, friendRequestResponse);
        create_alreadyFriends(accessTokenId, friendUserId);
    }

    private static void create_userNotFound(UUID accessTokenId) {
        Response create_userNotFoundResponse = FriendRequestActions.getCreateFriendRequestResponse(accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(create_userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void create_blocked(UUID accessTokenId, UUID blockedUserId) {
        BlacklistActions.createBlacklist(accessTokenId, blockedUserId);

        Response create_blockedResponse = FriendRequestActions.getCreateFriendRequestResponse(accessTokenId, blockedUserId);

        ResponseValidator.verifyForbiddenOperation(create_blockedResponse);
    }

    private static FriendRequestResponse create(UUID accessTokenId, RegistrationParameters friendUserData, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(accessTokenId, friendUserId);

        assertThat(friendRequestResponse.getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendRequestResponse.getEmail()).isEqualTo(friendUserData.getEmail());
        return friendRequestResponse;
    }

    private static void create_alreadyExists(UUID accessTokenId, UUID friendUserId) {
        Response create_alreadyExistsResponse = FriendRequestActions.getCreateFriendRequestResponse(accessTokenId, friendUserId);

        ResponseValidator.verifyErrorResponse(create_alreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static void query_sent(UUID accessTokenId, FriendRequestResponse friendRequestResponse) {
        List<FriendRequestResponse> sentFriendRequests = FriendRequestActions.getSentFriendRequests(accessTokenId);

        assertThat(sentFriendRequests).containsExactly(friendRequestResponse);
    }

    private static void query_received(RegistrationParameters userData, UUID friendUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        List<FriendRequestResponse> receivedFriendRequests = FriendRequestActions.getReceivedFriendRequests(friendUserAccessTokenId);

        FriendRequestResponse expected = FriendRequestResponse.builder()
            .friendRequestId(friendRequestResponse.getFriendRequestId())
            .username(userData.getUsername())
            .email(userData.getEmail())
            .build();
        assertThat(receivedFriendRequests).containsExactly(expected);
    }

    private static void delete_notFound(UUID accessTokenId) {
        Response delete_notFoundResponse = FriendRequestActions.getDeleteFriendRequestResponse(accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(UUID blockedUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        Response delete_forbiddenOperationResponse = FriendRequestActions.getDeleteFriendRequestResponse(blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(delete_forbiddenOperationResponse);
    }

    private static void deleteBySender(UUID accessTokenId, UUID friendUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendRequestActions.deleteFriendRequest(accessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(friendUserAccessTokenId)).isEmpty();
    }

    private static void deleteByReceiver(UUID accessTokenId, UUID friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(accessTokenId, friendUserId);

        FriendRequestActions.deleteFriendRequest(friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(friendUserAccessTokenId)).isEmpty();
    }

    private static FriendRequestResponse accept_notFound(UUID accessTokenId, UUID friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(accessTokenId, friendUserId);

        Response accept_notFoundResponse = FriendRequestActions.getAcceptFriendRequestResponse(friendUserAccessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(accept_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
        return friendRequestResponse;
    }

    private static void accept_forbiddenOperation(UUID blockedUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        Response accept_forbiddenOperationResponse = FriendRequestActions.getAcceptFriendRequestResponse(blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(accept_forbiddenOperationResponse);
    }

    private static void accept(RegistrationParameters userData, UUID accessTokenId, UUID friendUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(friendshipResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(friendshipResponse.getEmail()).isEqualTo(userData.getEmail());

        assertThat(FriendRequestActions.getSentFriendRequests(accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(friendUserAccessTokenId)).isEmpty();

        assertThat(FriendshipActions.getFriendships(accessTokenId)).hasSize(1);
        assertThat(FriendshipActions.getFriendships(friendUserAccessTokenId)).hasSize(1);
    }

    private static void create_alreadyFriends(UUID accessTokenId, UUID friendUserId) {
        Response create_alreadyFriendsResponse = FriendRequestActions.getCreateFriendRequestResponse(accessTokenId, friendUserId);

        ResponseValidator.verifyErrorResponse(create_alreadyFriendsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }
}
