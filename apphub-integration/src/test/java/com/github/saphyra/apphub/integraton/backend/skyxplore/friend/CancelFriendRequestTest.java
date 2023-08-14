package com.github.saphyra.apphub.integraton.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
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
    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void cancelFriendRequest(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);

        SkyXploreCharacterModel model1 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, model1);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, model3);

        SkyXploreFriendActions.createFriendRequest(language, accessTokenId1, userId2);

        //FriendRequest not found
        Response friendRequestNotFoundResponse = SkyXploreFriendActions.getCancelFriendRequestResponse(language, accessTokenId1, UUID.randomUUID());
        verifyErrorResponse(language, friendRequestNotFoundResponse, 404, ErrorCode.FRIEND_REQUEST_NOT_FOUND);

        //Forbidden operation
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        Response forbiddenOperationResponse = SkyXploreFriendActions.getCancelFriendRequestResponse(language, accessTokenId3, friendRequestId);
        verifyForbiddenOperation(language, forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)).hasSize(1);
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(language, accessTokenId2)).hasSize(1);

        //Cancel
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        SkyXploreFriendActions.cancelFriendRequest(language, accessTokenId1, friendRequestId);

        assertThat(SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)).isEmpty();
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(language, accessTokenId2)).isEmpty();

        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED).get().getPayloadAs(UUID.class)).isEqualTo(friendRequestId);

        ApphubWsClient.cleanUpConnections();
    }
}
