package com.github.saphyra.apphub.integration.backend.community.friendship;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
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

public class FriendshipCrudTest extends BackEndTest {
    @Test(groups = {"be", "community"})
    public void friendshipCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        RegistrationParameters friendUserData = RegistrationParameters.validParameters();
        UUID friendUserAccessTokenId = IndexPageActions.registerAndLogin(friendUserData);
        UUID friendUserId = DatabaseUtil.getUserIdByEmail(friendUserData.getEmail());

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        UUID testUserAccessTokenId = IndexPageActions.registerAndLogin(testUserData);

        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(accessTokenId, friendUserId);
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        queryBySender(accessTokenId, friendUserData, friendshipResponse);
        queryByReceiver(friendUserAccessTokenId, friendshipResponse);
        delete_notFound(accessTokenId);
        delete_forbiddenOperation(testUserAccessTokenId, friendshipResponse);
        deleteBySender(accessTokenId, friendUserAccessTokenId, friendshipResponse);
        deleteByReceiver(accessTokenId, friendUserAccessTokenId, friendUserId);
    }

    private static void queryBySender(UUID accessTokenId, RegistrationParameters friendUserData, FriendshipResponse friendshipResponse) {
        List<FriendshipResponse> friendshipsOfSender = FriendshipActions.getFriendships(accessTokenId);

        assertThat(friendshipsOfSender).hasSize(1);
        assertThat(friendshipsOfSender.get(0).getFriendshipId()).isEqualTo(friendshipResponse.getFriendshipId());
        assertThat(friendshipsOfSender.get(0).getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendshipsOfSender.get(0).getEmail()).isEqualTo(friendUserData.getEmail());
    }

    private static void queryByReceiver(UUID friendUserAccessTokenId, FriendshipResponse friendshipResponse) {
        List<FriendshipResponse> friendshipsOfReceiver = FriendshipActions.getFriendships(friendUserAccessTokenId);

        assertThat(friendshipsOfReceiver).containsExactly(friendshipResponse);
    }

    private static void delete_notFound(UUID accessTokenId) {
        Response delete_notFoundResponse = FriendshipActions.getDeleteFriendshipResponse(accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(UUID testUserAccessTokenId, FriendshipResponse friendshipResponse) {
        Response delete_forbiddenOperationResponse = FriendshipActions.getDeleteFriendshipResponse(testUserAccessTokenId, friendshipResponse.getFriendshipId());

        ResponseValidator.verifyForbiddenOperation(delete_forbiddenOperationResponse);
    }

    private static void deleteBySender(UUID accessTokenId, UUID friendUserAccessTokenId, FriendshipResponse friendshipResponse) {
        FriendshipActions.deleteFriendship(accessTokenId, friendshipResponse.getFriendshipId());

        assertThat(FriendshipActions.getFriendships(accessTokenId)).isEmpty();
        assertThat(FriendshipActions.getFriendships(friendUserAccessTokenId)).isEmpty();
    }

    private static void deleteByReceiver(UUID accessTokenId, UUID friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        FriendshipResponse friendshipResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(accessTokenId, friendUserId);
        friendshipResponse = FriendRequestActions.acceptFriendRequest(friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        FriendshipActions.deleteFriendship(friendUserAccessTokenId, friendshipResponse.getFriendshipId());

        assertThat(FriendshipActions.getFriendships(accessTokenId)).isEmpty();
        assertThat(FriendshipActions.getFriendships(friendUserAccessTokenId)).isEmpty();
    }
}
