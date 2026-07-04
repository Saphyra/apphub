package com.github.saphyra.apphub.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetFriendRequestsTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void getFriendRequests() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        String accessToken2 = IndexPageActions.registerAndLogin(getServerPort(), userData2);
        UUID userId2 = UserDynamoDbRepository.getUserIdByEmail(userData2.getEmail());

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, model);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid();
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken2, model2);

        SkyXploreFriendActions.createFriendRequest(getServerPort(), accessToken1, userId2);

        List<SentFriendRequestResponse> sentFriendRequests = SkyXploreFriendActions.getSentFriendRequests(getServerPort(), accessToken1);
        assertThat(sentFriendRequests).hasSize(1);
        assertThat(sentFriendRequests.get(0).getFriendName()).isEqualTo(model2.getName());

        List<IncomingFriendRequestResponse> incomingFriendRequests = SkyXploreFriendActions.getIncomingFriendRequests(getServerPort(), accessToken2);
        assertThat(incomingFriendRequests).hasSize(1);
        assertThat(incomingFriendRequests.get(0).getSenderName()).isEqualTo(model.getName());
    }
}
