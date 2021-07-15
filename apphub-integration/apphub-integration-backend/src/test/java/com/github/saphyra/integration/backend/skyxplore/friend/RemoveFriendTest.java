package com.github.saphyra.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyForbiddenOperation;
import static org.assertj.core.api.Assertions.assertThat;

public class RemoveFriendTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void removeFriend(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, model3);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        //Friend not found
        Response friendNotFoundResponse = SkyXploreFriendActions.getRemoveFriendResponse(language, accessTokenId, UUID.randomUUID());
        verifyErrorResponse(language, friendNotFoundResponse, 404, ErrorCode.FRIENDSHIP_NOT_FOUND);

        //Forbidden operation
        SkyXploreFriendActions.createFriendRequest(language, accessTokenId, userId2);
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(language, accessTokenId2, friendRequestId);

        UUID friendshipId = SkyXploreFriendActions.getFriends(language, accessTokenId)
            .stream()
            .map(FriendshipResponse::getFriendshipId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friendship not found"));

        Response forbiddenOperationResponse = SkyXploreFriendActions.getRemoveFriendResponse(language, accessTokenId3, friendshipId);
        verifyForbiddenOperation(language, forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId)).hasSize(1);
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId2)).hasSize(1);

        //Remove
        ApphubWsClient senderClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId);
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        Response removeResponse = SkyXploreFriendActions.getRemoveFriendResponse(language, accessTokenId, friendshipId);
        assertThat(removeResponse.getStatusCode()).isEqualTo(200);
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId)).isEmpty();
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId2)).isEmpty();

        assertThat(senderClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED)).isPresent();
        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED)).isPresent();
    }
}
