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

        //Create - User not found
        Response create_userNotFoundResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, create_userNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);

        //Create - Blocked
        BlacklistActions.createBlacklist(language, accessTokenId, blockedUserId);

        Response create_blockedResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, blockedUserId);

        ResponseValidator.verifyForbiddenOperation(language, create_blockedResponse);

        //Create
        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);

        assertThat(friendRequestResponse.getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendRequestResponse.getEmail()).isEqualTo(friendUserData.getEmail());

        //Create - Already exists
        Response create_alreadyExistsResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, friendUserId);

        ResponseValidator.verifyErrorResponse(language, create_alreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);

        //Query - sent
        List<FriendRequestResponse> sentFriendRequests = FriendRequestActions.getSentFriendRequests(language, accessTokenId);

        assertThat(sentFriendRequests).containsExactly(friendRequestResponse);

        //Query - received
        List<FriendRequestResponse> receivedFriendRequests = FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId);

        FriendRequestResponse expected = FriendRequestResponse.builder()
            .friendRequestId(friendRequestResponse.getFriendRequestId())
            .username(userData.getUsername())
            .email(userData.getEmail())
            .build();
        assertThat(receivedFriendRequests).containsExactly(expected);

        //Delete - Not found
        Response delete_notFoundResponse = FriendRequestActions.getDeleteFriendRequestResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);

        //Delete - Forbidden operation
        Response delete_forbiddenOperationResponse = FriendRequestActions.getDeleteFriendRequestResponse(language, blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(language, delete_forbiddenOperationResponse);

        //Delete by sender
        FriendRequestActions.deleteFriendRequest(language, accessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId)).isEmpty();

        //Delete by receiver
        friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);

        FriendRequestActions.deleteFriendRequest(language, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId)).isEmpty();

        //Accept - Not found
        friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);

        Response accept_notFoundResponse = FriendRequestActions.getAcceptFriendRequestResponse(language, friendUserAccessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, accept_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);

        //Accept - Forbidden operation
        Response accept_forbiddenOperationResponse = FriendRequestActions.getAcceptFriendRequestResponse(language, blockedUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        ResponseValidator.verifyForbiddenOperation(language, accept_forbiddenOperationResponse);

        //Accept
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(language, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        assertThat(friendshipResponse.getUsername()).isEqualTo(userData.getUsername());
        assertThat(friendshipResponse.getEmail()).isEqualTo(userData.getEmail());

        assertThat(FriendRequestActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(FriendRequestActions.getReceivedFriendRequests(language, friendUserAccessTokenId)).isEmpty();

        assertThat(FriendshipActions.getFriendships(language, accessTokenId)).hasSize(1);
        assertThat(FriendshipActions.getFriendships(language, friendUserAccessTokenId)).hasSize(1);

        //Create - Already Friends
        Response create_alreadyFriendsResponse = FriendRequestActions.getCreateFriendRequestResponse(language, accessTokenId, friendUserId);

        ResponseValidator.verifyErrorResponse(language, create_alreadyFriendsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }
}
