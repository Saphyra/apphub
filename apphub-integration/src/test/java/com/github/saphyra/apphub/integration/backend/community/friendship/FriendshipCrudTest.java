package com.github.saphyra.apphub.integration.backend.community.friendship;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendRequestActions;
import com.github.saphyra.apphub.integration.action.backend.community.FriendshipActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.community.FriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.community.FriendshipResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class FriendshipCrudTest extends BackEndTest {
    @Test(groups = {"be", "community"})
    public void friendshipCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        int serverPort = getServerPort();
        String accessToken = IndexPageActions.registerAndLogin(serverPort, userData);

        RegistrationParameters friendUserData = RegistrationParameters.validParameters();
        String friendUserAccessTokenId = IndexPageActions.registerAndLogin(serverPort, friendUserData);
        UUID friendUserId = UserDynamoDbRepository.getUserIdByEmail(friendUserData.getEmail());

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        String testUserAccessTokenId = IndexPageActions.registerAndLogin(serverPort, testUserData);

        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(serverPort, accessToken, friendUserId);
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(serverPort, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        queryBySender(accessToken, friendUserData, friendshipResponse);
        queryByReceiver(friendUserAccessTokenId, friendshipResponse);
        delete_notFound(accessToken);
        delete_forbiddenOperation(testUserAccessTokenId, friendshipResponse);
        deleteBySender(accessToken, friendUserAccessTokenId, friendshipResponse);
        deleteByReceiver(accessToken, friendUserAccessTokenId, friendUserId);
    }

    private static void queryBySender(String accessToken, RegistrationParameters friendUserData, FriendshipResponse friendshipResponse) {
        List<FriendshipResponse> friendshipsOfSender = FriendshipActions.getFriendships(getServerPort(), accessToken);

        assertThat(friendshipsOfSender).hasSize(1);
        assertThat(friendshipsOfSender.getFirst().getFriendshipId()).isEqualTo(friendshipResponse.getFriendshipId());
        assertThat(friendshipsOfSender.getFirst().getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendshipsOfSender.getFirst().getEmail()).isEqualTo(friendUserData.getEmail());
    }

    private static void queryByReceiver(String friendUserAccessTokenId, FriendshipResponse friendshipResponse) {
        List<FriendshipResponse> friendshipsOfReceiver = FriendshipActions.getFriendships(getServerPort(), friendUserAccessTokenId);

        assertThat(friendshipsOfReceiver).containsExactly(friendshipResponse);
    }

    private static void delete_notFound(String accessToken) {
        Response delete_notFoundResponse = FriendshipActions.getDeleteFriendshipResponse(getServerPort(), accessToken, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(String testUserAccessTokenId, FriendshipResponse friendshipResponse) {
        Response delete_forbiddenOperationResponse = FriendshipActions.getDeleteFriendshipResponse(getServerPort(), testUserAccessTokenId, friendshipResponse.getFriendshipId());

        ResponseValidator.verifyForbiddenOperation(delete_forbiddenOperationResponse);
    }

    private static void deleteBySender(String accessToken, String friendUserAccessTokenId, FriendshipResponse friendshipResponse) {
        FriendshipActions.deleteFriendship(getServerPort(), accessToken, friendshipResponse.getFriendshipId());

        assertThat(FriendshipActions.getFriendships(getServerPort(), accessToken)).isEmpty();
        assertThat(FriendshipActions.getFriendships(getServerPort(), friendUserAccessTokenId)).isEmpty();
    }

    private static void deleteByReceiver(String accessToken, String friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        FriendshipResponse friendshipResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(getServerPort(), accessToken, friendUserId);
        friendshipResponse = FriendRequestActions.acceptFriendRequest(getServerPort(), friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        FriendshipActions.deleteFriendship(getServerPort(), friendUserAccessTokenId, friendshipResponse.getFriendshipId());

        assertThat(FriendshipActions.getFriendships(getServerPort(), accessToken)).isEmpty();
        assertThat(FriendshipActions.getFriendships(getServerPort(), friendUserAccessTokenId)).isEmpty();
    }
}
