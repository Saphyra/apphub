package com.github.saphyra.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.FriendshipResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AcceptFriendRequestTest extends BackEndTest {
    @Test(dataProvider = "localeDataProvider")
    public void friendRequestNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        Response response = SkyXploreFriendActions.getAcceptFriendRequestResponse(language, accessTokenId, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FRIEND_REQUEST_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FRIEND_REQUEST_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void forbiddenOperation(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, model3);

        SkyXploreFriendActions.createFriendRequest(language, accessTokenId, userId2);

        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));

        Response response = SkyXploreFriendActions.getAcceptFriendRequestResponse(language, accessTokenId3, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(403);
        ErrorResponse errorResponse = response.getBody()
            .as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.FORBIDDEN_OPERATION));
    }

    @Test
    public void acceptFriendRequest() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreFriendActions.createFriendRequest(language, accessTokenId, userId2);

        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));

        Response response = SkyXploreFriendActions.getAcceptFriendRequestResponse(language, accessTokenId2, friendRequestId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId)).isEmpty();
        assertThat(SkyXploreFriendActions.getIncomingFriendRequests(language, accessTokenId2)).isEmpty();

        List<FriendshipResponse> senderFriendships = SkyXploreFriendActions.getFriends(language, accessTokenId);
        assertThat(senderFriendships).hasSize(1);
        assertThat(senderFriendships.get(0).getFriendName()).isEqualTo(model2.getName());

        List<FriendshipResponse> receiverFriendships = SkyXploreFriendActions.getFriends(language, accessTokenId2);
        assertThat(receiverFriendships).hasSize(1);
        assertThat(receiverFriendships.get(0).getFriendName()).isEqualTo(model.getName());
    }
}
