package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFriendActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ActiveFriendResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

public class GetActiveFriendsTest extends BackEndTest {
    @Test
    public void getActiveFriends() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(language, userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreFriendActions.setUpFriendship(language, accessTokenId1, accessTokenId2, userId2);

        getSoftAssertions().assertThat(SkyXploreFriendActions.getActiveFriends(language, accessTokenId1)).isEmpty();

        ApphubWsClient.createSkyXploreMainMenu(language, accessTokenId2);

        List<ActiveFriendResponse> activeFriends = SkyXploreFriendActions.getActiveFriends(language, accessTokenId1);
        getSoftAssertions().assertThat(activeFriends).hasSize(1);
        getSoftAssertions().assertThat(activeFriends).containsExactly(ActiveFriendResponse.builder().friendId(userId2).friendName(characterModel2.getName()).build());
    }
}
