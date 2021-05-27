package com.github.saphyra.integration.backend.skyxplore.friend;

import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SentFriendRequestResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetFriendCandidatesTest extends BackEndTest {
    private static final String CHARACTER_IDENTIFIER = UUID.randomUUID().toString().substring(0, 6);

    @Test(groups = "skyxplore")
    public void getFriendCandidates() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        RegistrationParameters userData3 = RegistrationParameters.validParameters();
        UUID accessTokenId3 = IndexPageActions.registerAndLogin(language, userData3);
        UUID userId3 = DatabaseUtil.getUserIdByEmail(userData3.getEmail());

        RegistrationParameters userData4 = RegistrationParameters.validParameters();
        UUID accessTokenId4 = IndexPageActions.registerAndLogin(language, userData4);
        UUID userId4 = DatabaseUtil.getUserIdByEmail(userData4.getEmail());

        SkyXploreCharacterModel model = SkyXploreCharacterModel.valid(CHARACTER_IDENTIFIER);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, model);

        SkyXploreCharacterModel model2 = SkyXploreCharacterModel.valid(CHARACTER_IDENTIFIER);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, model2);

        SkyXploreCharacterModel model3 = SkyXploreCharacterModel.valid(CHARACTER_IDENTIFIER);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId3, model3);

        SkyXploreCharacterModel model4 = SkyXploreCharacterModel.valid(CHARACTER_IDENTIFIER);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId4, model4);

        SkyXploreFriendActions.createFriendRequest(language, accessTokenId, userId2);

        SkyXploreFriendActions.createFriendRequest(language, accessTokenId, userId3);
        UUID friendRequestId = SkyXploreFriendActions.getSentFriendRequests(language, accessTokenId)
            .stream()
            .filter(sentFriendRequestResponse -> sentFriendRequestResponse.getFriendName().equals(model3.getName()))
            .map(SentFriendRequestResponse::getFriendRequestId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("FriendRequest not found"));
        SkyXploreFriendActions.acceptFriendRequest(language, accessTokenId3, friendRequestId);

        List<SkyXploreCharacterModel> result = SkyXploreFriendActions.getFriendCandidates(language, accessTokenId, CHARACTER_IDENTIFIER.toUpperCase());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(userId4);
        assertThat(result.get(0).getName()).isEqualTo(model4.getName());
    }
}
