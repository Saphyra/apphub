package com.github.saphyra.apphub.integraton.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.FriendshipResponse;
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

public class RemoveFriendTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void removeFriend(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, model);

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, model3);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        //Friend not found
        Response friendNotFoundResponse = SkyXploreFriendActions.getRemoveFriendResponse(language, accessTokenId1, UUID.randomUUID());
        verifyErrorResponse(language, friendNotFoundResponse, 404, ErrorCode.FRIENDSHIP_NOT_FOUND);

        //Forbidden operation
        SkyXploreFriendActions.createFriendRequest(language, accessTokenId1, userId2);
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(language, accessTokenId2, friendRequestId);

        UUID friendshipId = SkyXploreFriendActions.getFriends(language, accessTokenId1)
            .stream()
            .map(FriendshipResponse::getFriendshipId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Friendship not found"));

        Response forbiddenOperationResponse = SkyXploreFriendActions.getRemoveFriendResponse(language, accessTokenId3, friendshipId);
        verifyForbiddenOperation(language, forbiddenOperationResponse);
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId1)).hasSize(1);
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId2)).hasSize(1);

        //Remove
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        SkyXploreFriendActions.removeFriend(language, accessTokenId1, friendshipId);

        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId1)).isEmpty();
        assertThat(SkyXploreFriendActions.getFriends(language, accessTokenId2)).isEmpty();

        assertThat(friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED).get().getPayloadAs(UUID.class)).isEqualTo(friendshipId);

        ApphubWsClient.cleanUpConnections();
    }
}
