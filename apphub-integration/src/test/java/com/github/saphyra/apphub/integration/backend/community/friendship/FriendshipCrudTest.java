package com.github.saphyra.apphub.integration.backend.community.friendship;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
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

public class FriendshipCrudTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "community"})
    public void friendshipCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters friendUserData = RegistrationParameters.validParameters();
        UUID friendUserAccessTokenId = IndexPageActions.registerAndLogin(language, friendUserData);
        UUID friendUserId = DatabaseUtil.getUserIdByEmail(friendUserData.getEmail());

        RegistrationParameters testUserData = RegistrationParameters.validParameters();
        UUID testUserAccessTokenId = IndexPageActions.registerAndLogin(language, testUserData);

        FriendRequestResponse friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);
        FriendshipResponse friendshipResponse = FriendRequestActions.acceptFriendRequest(language, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        queryBySender(language, accessTokenId, friendUserData, friendshipResponse);
        queryByReceiver(language, friendUserAccessTokenId, friendshipResponse);
        delete_notFound(language, accessTokenId);
        delete_forbiddenOperation(language, testUserAccessTokenId, friendshipResponse);
        deleteBySender(language, accessTokenId, friendUserAccessTokenId, friendshipResponse);
        deleteByReceiver(language, accessTokenId, friendUserAccessTokenId, friendUserId);
    }

    private static void queryBySender(Language language, UUID accessTokenId, RegistrationParameters friendUserData, FriendshipResponse friendshipResponse) {
        List<FriendshipResponse> friendshipsOfSender = FriendshipActions.getFriendships(language, accessTokenId);

        assertThat(friendshipsOfSender).hasSize(1);
        assertThat(friendshipsOfSender.get(0).getFriendshipId()).isEqualTo(friendshipResponse.getFriendshipId());
        assertThat(friendshipsOfSender.get(0).getUsername()).isEqualTo(friendUserData.getUsername());
        assertThat(friendshipsOfSender.get(0).getEmail()).isEqualTo(friendUserData.getEmail());
    }

    private static void queryByReceiver(Language language, UUID friendUserAccessTokenId, FriendshipResponse friendshipResponse) {
        List<FriendshipResponse> friendshipsOfReceiver = FriendshipActions.getFriendships(language, friendUserAccessTokenId);

        assertThat(friendshipsOfReceiver).containsExactly(friendshipResponse);
    }

    private static void delete_notFound(Language language, UUID accessTokenId) {
        Response delete_notFoundResponse = FriendshipActions.getDeleteFriendshipResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyErrorResponse(language, delete_notFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void delete_forbiddenOperation(Language language, UUID testUserAccessTokenId, FriendshipResponse friendshipResponse) {
        Response delete_forbiddenOperationResponse = FriendshipActions.getDeleteFriendshipResponse(language, testUserAccessTokenId, friendshipResponse.getFriendshipId());

        ResponseValidator.verifyForbiddenOperation(language, delete_forbiddenOperationResponse);
    }

    private static void deleteBySender(Language language, UUID accessTokenId, UUID friendUserAccessTokenId, FriendshipResponse friendshipResponse) {
        FriendshipActions.deleteFriendship(language, accessTokenId, friendshipResponse.getFriendshipId());

        assertThat(FriendshipActions.getFriendships(language, accessTokenId)).isEmpty();
        assertThat(FriendshipActions.getFriendships(language, friendUserAccessTokenId)).isEmpty();
    }

    private static void deleteByReceiver(Language language, UUID accessTokenId, UUID friendUserAccessTokenId, UUID friendUserId) {
        FriendRequestResponse friendRequestResponse;
        FriendshipResponse friendshipResponse;
        friendRequestResponse = FriendRequestActions.createFriendRequest(language, accessTokenId, friendUserId);
        friendshipResponse = FriendRequestActions.acceptFriendRequest(language, friendUserAccessTokenId, friendRequestResponse.getFriendRequestId());

        FriendshipActions.deleteFriendship(language, friendUserAccessTokenId, friendshipResponse.getFriendshipId());

        assertThat(FriendshipActions.getFriendships(language, accessTokenId)).isEmpty();
        assertThat(FriendshipActions.getFriendships(language, friendUserAccessTokenId)).isEmpty();
    }
}