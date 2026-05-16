package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetFriendCandidatesTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void getFriendCandidates() {
        String characterIdentifier = UUID.randomUUID().toString().substring(0, 6);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = DynamoDbUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        String accessToken3 = IndexPageActions.registerAndLogin(getServerPort(), userData3);
        UUID userId3 = DynamoDbUtil.getUserIdByEmail(userData3.getEmail());

        RegistrationParameters userData4 = RegistrationParameters.validParameters();
        String accessToken4 = IndexPageActions.registerAndLogin(getServerPort(), userData4);
        UUID userId4 = DynamoDbUtil.getUserIdByEmail(userData4.getEmail());

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid(characterIdentifier);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, model);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid(characterIdentifier);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, model2);

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid(characterIdentifier);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken3, model3);

        SkyXploreCharacterModel model4 = SkyXploreCharacterModel.valid(characterIdentifier);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken4, model4);

        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken, userId2);

        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken, userId3);
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken)
            .stream()
            .filter(sentFriendRequestResponse -> sentFriendRequestResponse.getFriendName().equals(model3.getName()))
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(getServerPort(), accessToken3, friendRequestId);

        List<SkyXploreCharacterModel> result = SkyXploreFriendActions.getFriendCandidates(getServerPort(), accessToken, characterIdentifier.toUpperCase());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(userId4);
        assertThat(result.get(0).getName()).isEqualTo(model4.getName());
    }
}
