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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters friendUserData = RegistrationParameters.validParameters();
        String friendUserAccessToken = IndexPageActions.registerAndLogin(getServerPort(), friendUserData);
        UUID friendUserId = DatabaseUtil.getUserIdByEmail(friendUserData.getEmail());

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        String blockedUserAccessToken = IndexPageActions.registerAndLogin(getServerPort(), blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        create_userNotFound(accessToken);
        create_blocked(accessToken, blockedUserId);
        FriendRequestResponse friendRequestResponse = create(accessToken, friendUserData, friendUserId);
        create_alreadyExists(accessToken, friendUserId);
        query_sent(accessToken, friendRequestResponse);
        query_received(userData, friendUserAccessToken, friendRequestResponse);
        delete_notFound(accessToken);
        delete_forbiddenOperation(blockedUserAccessToken, friendRequestResponse);
        deleteBySender(accessToken, friendUserAccessToken, friendRequestResponse);
        deleteByReceiver(accessToken, friendUserAccessToken, friendUserId);
        friendRequestResponse = accept_notFound(accessToken, friendUserAccessToken, friendUserId);
        accept_forbiddenOperation(blockedUserAccessToken, friendRequestResponse);
        accept(userData, accessToken, friendUserAccessToken, friendRequestResponse);
        create_alreadyFriends(accessToken, friendUserId);
    }

    private static void create_userNotFound(String accessToken) {
        Response create_userNotFoundResponse = FriendRequestActions.getCreateFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(create_userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void create_blocked(String accessToken, UUID blockedUserId) {
        BlacklistActions.createBlacklist(getServerPort(), accessToken, blockedUserId);

        Response create_blockedResponse = FriendRequestActions.getCreateFriendRequestResponse(getServerPort(), accessToken, blockedUserId);

        ResponseValidator.verifyForbiddenOperation(create_blockedResponse);
    }

    private static FriendRequestResponse create(String accessToken, RegistrationParameters friendUserData, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessToken, friendUserId);

        assertThat(friendRequestResponse.getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendRequestResponse.getEmail()).isEqualTo(friendUserData.getEmail());
        return friendRequestResponse;
    }

    private static void create_alreadyExists(String accessToken, UUID friendUserId) {
        Response create_alreadyExistsResponse = FriendRequestActions.getCreateFriendRequestResponse(getServerPort(), accessToken, friendUserId);

        ResponseValidator.verifyErrorResponse(create_alreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static void query_sent(String accessToken, FriendRequestResponse friendRequestResponse) {
        List<FriendRequestResponse> sentFriendRequests = FriendRequestActions.getSentFriendRequests(getServerPort(), accessToken);

        assertThat(sentFriendRequests).containsExactly(friendRequestResponse);
    }

    private static void query_received(RegistrationParameters userData, String friendUserAccessToken, FriendRequestResponse friendRequestResponse) {
        List<FriendRequestResponse> receivedFriendRequests = FriendRequestActions.getReceivedFriendRequests(getServerPort(), friendUserAccessToken);

        FriendRequestResponse expected = FriendRequestResponse.builder()
            .friendRequestId(friendRequestResponse.getFriendRequestId())
            .username(userData.getUsername())
            .email(userData.getEmail())
            .build();
        assertThat(receivedFriendRequests).containsExactly(expected);
    }

    private static void delete_notFound(String accessToken) {
        Response delete_notFoundResponse = FriendRequestActions.getDeleteFriendRequestResponse(getServerPort(), accessToken, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(String blockedUserAccessToken, FriendRequestResponse friendRequestResponse) {
        Response delete_forbiddenOperationResponse = FriendRequestActions.getDeleteFriendRequestResponse(getServerPort(), blockedUserAccessToken, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(delete_forbiddenOperationResponse);
    }

    private static void deleteBySender(String accessToken, String friendUserAccessToken, FriendRequestResponse friendRequestResponse) {
        FriendRequestActions.deleteFriendRequest(getServerPort(), accessToken, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(getServerPort(), accessToken)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(getServerPort(), friendUserAccessToken)).isEmpty();
    }

    private static void deleteByReceiver(String accessToken, String friendUserAccessToken, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessToken, friendUserId);

        FriendRequestActions.deleteFriendRequest(getServerPort(), friendUserAccessToken, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(getServerPort(), accessToken)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(getServerPort(), friendUserAccessToken)).isEmpty();
    }

    private static FriendRequestResponse accept_notFound(String accessToken, String friendUserAccessToken, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessToken, friendUserId);

        Response accept_notFoundResponse = FriendRequestActions.getAcceptFriendRequestResponse(getServerPort(), friendUserAccessToken, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(accept_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
        return friendRequestResponse;
    }

    private static void accept_forbiddenOperation(String blockedUserAccessToken, FriendRequestResponse friendRequestResponse) {
        Response accept_forbiddenOperationResponse = FriendRequestActions.getAcceptFriendRequestResponse(getServerPort(), blockedUserAccessToken, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(accept_forbiddenOperationResponse);
    }

    private static void accept(RegistrationParameters userData, String accessToken, String friendUserAccessToken, FriendRequestResponse friendRequestResponse) {
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(getServerPort(), friendUserAccessToken, friendRequestResponse.getFriendRequestId());

        assertThat(friendshipResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(friendshipResponse.getEmail()).isEqualTo(userData.getEmail());

        assertThat(FriendRequestActions.getSentFriendRequests(getServerPort(), accessToken)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(getServerPort(), friendUserAccessToken)).isEmpty();

        assertThat(FriendshipActions.getFriendships(getServerPort(), accessToken)).hasSize(1);
        assertThat(FriendshipActions.getFriendships(getServerPort(), friendUserAccessToken)).hasSize(1);
    }

    private static void create_alreadyFriends(String accessToken, UUID friendUserId) {
        Response create_alreadyFriendsResponse = FriendRequestActions.getCreateFriendRequestResponse(getServerPort(), accessToken, friendUserId);

        ResponseValidator.verifyErrorResponse(create_alreadyFriendsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }
}
