package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
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
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);

        SkyXploreCharacterModel model1 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, model1);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, model2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, model3);

        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken1, userId2);

        friendRequestNotFound(accessToken1);
        UUID friendRequestId = forbiddenOperation(accessToken1, accessToken2, accessToken3);
        cancel(accessToken1, accessToken2, friendRequestId);
    }

    private static void friendRequestNotFound(String accessToken1) {
        Response friendRequestNotFoundResponse = SkyXploreFriendActions.getCancelFriendRequestResponse(getServerPort(), accessToken1, UUID.randomUUID());
        verifyErrorResponse(friendRequestNotFoundResponse, 404, ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }

    private static UUID forbiddenOperation(String accessToken1, String accessToken2, String accessToken3) {
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        Response forbiddenOperationResponse = SkyXploreFriendActions.getCancelFriendRequestResponse(getServerPort(), accessToken3, friendRequestId);
        verifyForbiddenOperation(forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1)).hasSize(1);
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(getServerPort(), accessToken2)).hasSize(1);
        return friendRequestId;
    }

    private static void cancel(String accessToken1, String accessToken2, UUID friendRequestId) {
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(getServerPort(), accessToken2, accessToken2);

        SkyXploreFriendActions.cancelFriendRequest(getServerPort(), accessToken1, friendRequestId);

        assertThat(SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1)).isEmpty();
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(getServerPort(), accessToken2)).isEmpty();

        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED).get().getPayloadAs(UUID.class)).isEqualTo(friendRequestId);
    }
}
