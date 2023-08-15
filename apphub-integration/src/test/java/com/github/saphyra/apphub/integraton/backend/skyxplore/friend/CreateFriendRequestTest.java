package com.github.saphyra.apphub.integraton.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateFriendRequestTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void createFriendRequest(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        SkyXploreCharacterModel model1 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, model1);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        characterNotFound(language, accessTokenId1);
        createFriendRequest(language, accessTokenId1, accessTokenId2, model1, model2, userId2);
        friendRequestAlreadyExists(language, accessTokenId1, userId2);
        friendshipAlreadyExists(language, accessTokenId1, accessTokenId2, userId2);
    }

    private static void characterNotFound(Language language, UUID accessTokenId1) {
        Response characterNotFoundResponse = SkyXploreFriendActions.getCreateFriendRequestResponse(language, accessTokenId1, UUID.randomUUID());
        verifyErrorResponse(language, characterNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);
    }

    private static void createFriendRequest(Language language, UUID accessTokenId1, UUID accessTokenId2, SkyXploreCharacterModel model1, SkyXploreCharacterModel model2, UUID userId2) {
        ApphubWsClient friendClient = ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        SentFriendRequestResponse sentFriendRequestResponse = SkyXploreFriendActions.createFriendRequest(language, accessTokenId1, userId2);

        assertThat(sentFriendRequestResponse.getFriendName()).isEqualTo(model2.getName());

        List<SentFriendRequestResponse> sentFriendRequests = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1);
        assertThat(sentFriendRequests).hasSize(1);
        assertThat(sentFriendRequests.get(0).getFriendName()).isEqualTo(model2.getName());
        List<IncomingFriendRequestResponse> incomingFriendRequests = SkyXploreFriendActions.getIncomingFriendRequests(language, accessTokenId2);
        assertThat(incomingFriendRequests).hasSize(1);
        assertThat(incomingFriendRequests.get(0).getSenderName()).isEqualTo(model1.getName());

        IncomingFriendRequestResponse incomingFriendRequestResponse = friendClient.awaitForEvent(WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_SENT)
            .orElseThrow(() -> new RuntimeException("WsEvent not arrived"))
            .getPayloadAs(IncomingFriendRequestResponse.class);

        assertThat(incomingFriendRequestResponse.getSenderName()).isEqualTo(model1.getName());
    }

    private static void friendRequestAlreadyExists(Language language, UUID accessTokenId1, UUID userId2) {
        Response friendRequestAlreadyExistsResponse = SkyXploreFriendActions.getCreateFriendRequestResponse(language, accessTokenId1, userId2);
        verifyErrorResponse(language, friendRequestAlreadyExistsResponse, 409, ErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);
    }

    private static void friendshipAlreadyExists(Language language, UUID accessTokenId1, UUID accessTokenId2, UUID userId2) {
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId1)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(language, accessTokenId2, friendRequestId);
        Response friendshipAlreadyExistsResponse = SkyXploreFriendActions.getCreateFriendRequestResponse(language, accessTokenId1, userId2);
        verifyErrorResponse(language, friendshipAlreadyExistsResponse, 409, ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
    }
}
