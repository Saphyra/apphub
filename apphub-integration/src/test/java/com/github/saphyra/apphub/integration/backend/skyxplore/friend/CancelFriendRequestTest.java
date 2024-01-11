package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyForbiddenOperation;
import static org.assertj.core.api.Assertions.assertThat;

public class CancelFriendRequestTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void cancelFriendRequest() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(userData3);

        SkyXploreCharacterModel model1 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId1, model1);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId3, model3);

        SkyXploreFriendActions.createFriendRequest(accessTokenId1, userId2);

        friendRequestNotFound(accessTokenId1);
        UUID friendRequestId = forbiddenOperation(accessTokenId1, accessTokenId2, accessTokenId3);
        cancel(accessTokenId1, accessTokenId2, friendRequestId);
    }

    private static void friendRequestNotFound(UUID accessTokenId1) {
        Response friendRequestNotFoundResponse = SkyXploreFriendActions.getCancelFriendRequestResponse(accessTokenId1, UUID.randomUUID());
        verifyErrorResponse(friendRequestNotFoundResponse, 404, ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    private static UUID forbiddenOperation(UUID accessTokenId1, UUID accessTokenId2, UUID accessTokenId3) {
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(accessTokenId1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        Response forbiddenOperationResponse = SkyXploreFriendActions.getCancelFriendRequestResponse(accessTokenId3, friendRequestId);
        verifyForbiddenOperation(forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getSentFriendRequests(accessTokenId1)).hasSize(1);
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(accessTokenId2)).hasSize(1);
        return friendRequestId;
    }

    private static void cancel(UUID accessTokenId1, UUID accessTokenId2, UUID friendRequestId) {
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(accessTokenId2, accessTokenId2);

        SkyXploreFriendActions.cancelFriendRequest(accessTokenId1, friendRequestId);

        assertThat(SkyXploreFriendActions.getSentFriendRequests(accessTokenId1)).isEmpty();
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(accessTokenId2)).isEmpty();

        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED).get().getPayloadAs(UUID.class)).isEqualTo(friendRequestId);
    }
}
