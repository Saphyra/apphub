package com.github.saphyra.apphub.integraton.backend.community.friend_request;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.BlacklistActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.community.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendRequestCrudTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "community"})
    public void friendRequestCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters friendUserData = RegistrationParameters.validParameters();
        UUID friendUserAccessTokenId = IndexPageActions.registerAndLogin(language, friendUserData);
        UUID friendUserId = DatabaseUtil.getUserIdByEmail(friendUserData.getEmail());

        RegistrationParameters blockedUserData = RegistrationParameters.validParameters();
        UUID blockedUserAccessTokenId = IndexPageActions.registerAndLogin(language, blockedUserData);
        UUID blockedUserId = DatabaseUtil.getUserIdByEmail(blockedUserData.getEmail());

        create_userNotFound(language, accessTokenId);
        create_blocked(language, accessTokenId, blockedUserId);
        FriendRequestResponse friendRequestResponse = create(language, accessTokenId, friendUserData, friendUserId);
        create_alreadyExists(language, accessTokenId, friendUserId);
        query_sent(language, accessTokenId, friendRequestResponse);
        query_received(language, userData, friendUserAccessTokenId, friendRequestResponse);
        delete_notFound(language, accessTokenId);
        delete_forbiddenOperation(language, blockedUserAccessTokenId, friendRequestResponse);
        deleteBySender(language, accessTokenId, friendUserAccessTokenId, friendRequestResponse);
        deleteByReceiver(language, accessTokenId, friendUserAccessTokenId, friendUserId);
        friendRequestResponse = accept_notFound(language, accessTokenId, friendUserAccessTokenId, friendUserId);
        accept_forbiddenOperation(language, blockedUserAccessTokenId, friendRequestResponse);
        accept(language, userData, accessTokenId, friendUserAccessTokenId, friendRequestResponse);
        create_alreadyFriends(language, accessTokenId, friendUserId);
    }

    private static void create_userNotFound(Language language, UUID accessTokenId) {
        Response create_userNotFoundResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, create_userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void create_blocked(Language language, UUID accessTokenId, UUID blockedUserId) {
        BlacklistActions.createBlacklist(language, accessTokenId, blockedUserId);

        Response create_blockedResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, blockedUserId);

        ResponseValidator.verifyForbiddenOperation(language, create_blockedResponse);
    }

    private static FriendRequestResponse create(Language language, UUID accessTokenId, RegistrationParameters friendUserData, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);

        assertThat(friendRequestResponse.getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendRequestResponse.getEmail()).isEqualTo(friendUserData.getEmail());
        return friendRequestResponse;
    }

    private static void create_alreadyExists(Language language, UUID accessTokenId, UUID friendUserId) {
        Response create_alreadyExistsResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, friendUserId);

        ResponseValidator.verifyErrorResponse(language, create_alreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static void query_sent(Language language, UUID accessTokenId, FriendRequestResponse friendRequestResponse) {
        List<FriendRequestResponse> sentFriendRequests = FriendRequestActions.getSentFriendRequests(language, accessTokenId);

        assertThat(sentFriendRequests).containsExactly(friendRequestResponse);
    }

    private static void query_received(Language language, RegistrationParameters userData, UUID friendUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        List<FriendRequestResponse> receivedFriendRequests = FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId);

        FriendRequestResponse expected = FriendRequestResponse.builder()
            .friendRequestId(friendRequestResponse.getFriendRequestId())
            .username(userData.getUsername())
            .email(userData.getEmail())
            .build();
        assertThat(receivedFriendRequests).containsExactly(expected);
    }

    private static void delete_notFound(Language language, UUID accessTokenId) {
        Response delete_notFoundResponse = FriendRequestActions.getDeleteFriendRequestResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(Language language, UUID blockedUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        Response delete_forbiddenOperationResponse = FriendRequestActions.getDeleteFriendRequestResponse(language, blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(language, delete_forbiddenOperationResponse);
    }

    private static void deleteBySender(Language language, UUID accessTokenId, UUID friendUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendRequestActions.deleteFriendRequest(language, accessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId)).isEmpty();
    }

    private static void deleteByReceiver(Language language, UUID accessTokenId, UUID friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);

        FriendRequestActions.deleteFriendRequest(language, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId)).isEmpty();
    }

    private static FriendRequestResponse accept_notFound(Language language, UUID accessTokenId, UUID friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);

        Response accept_notFoundResponse = FriendRequestActions.getAcceptFriendRequestResponse(language, friendUserAccessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, accept_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
        return friendRequestResponse;
    }

    private static void accept_forbiddenOperation(Language language, UUID blockedUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        Response accept_forbiddenOperationResponse = FriendRequestActions.getAcceptFriendRequestResponse(language, blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(language, accept_forbiddenOperationResponse);
    }

    private static void accept(Language language, RegistrationParameters userData, UUID accessTokenId, UUID friendUserAccessTokenId, FriendRequestResponse friendRequestResponse) {
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(language, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(friendshipResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(friendshipResponse.getEmail()).isEqualTo(userData.getEmail());

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId)).isEmpty();

        assertThat(FriendshipActions.getFriendships(language, accessTokenId)).hasSize(1);
        assertThat(FriendshipActions.getFriendships(language, friendUserAccessTokenId)).hasSize(1);
    }

    private static void create_alreadyFriends(Language language, UUID accessTokenId, UUID friendUserId) {
        Response create_alreadyFriendsResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, friendUserId);

        ResponseValidator.verifyErrorResponse(language, create_alreadyFriendsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }
}
