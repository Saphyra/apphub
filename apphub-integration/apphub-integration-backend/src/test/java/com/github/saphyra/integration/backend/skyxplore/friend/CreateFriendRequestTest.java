package com.github.saphyra.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyErrorResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateFriendRequestTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void createFriendRequest(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        //Character not found
        Response characterNotFoundResponse = SkyXploreFriendActions.getCreateFriendRequestResponse(language, accessTokenId, UUID.randomUUID());
        verifyErrorResponse(language, characterNotFoundResponse, 404, ErrorCode.USER_NOT_FOUND);

        //Create friend request
        SkyXploreFriendActions.createFriendRequest(language, accessTokenId, userId2);
        List<SentFriendRequestResponse> sentFriendRequests = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId);
        assertThat(sentFriendRequests).hasSize(1);
        assertThat(sentFriendRequests.get(0).getFriendName()).isEqualTo(model2.getName());
        List<IncomingFriendRequestResponse> incomingFriendRequests = SkyXploreFriendActions.getIncomingFriendRequests(language, accessTokenId2);
        assertThat(incomingFriendRequests).hasSize(1);
        assertThat(incomingFriendRequests.get(0).getSenderName()).isEqualTo(model.getName());

        //Friend request already exists
        Response friendRequestAlreadyExistsResponse = SkyXploreFriendActions.getCreateFriendRequestResponse(language, accessTokenId, userId2);
        verifyErrorResponse(language, friendRequestAlreadyExistsResponse, 409, ErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);

        //Friendship already exists
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId)
            .stream()
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(language, accessTokenId2, friendRequestId);
        Response friendshipAlreadyExistsResponse = SkyXploreFriendActions.getCreateFriendRequestResponse(language, accessTokenId, userId2);
        verifyErrorResponse(language, friendshipAlreadyExistsResponse, 409, ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
    }
}
