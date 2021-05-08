package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.GameSettingsResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

public class GetGameSettingsTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test
    public void getGameSettings() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        GameSettingsResponse response = SkyXploreLobbyActions.getGameSettings(language, accessTokenId1);

        TestBase.getSoftAssertions().assertThat(response.getUniverseSize()).isEqualTo("SMALL");
        TestBase.getSoftAssertions().assertThat(response.getSystemAmount()).isEqualTo("MEDIUM");
        TestBase.getSoftAssertions().assertThat(response.getSystemSize()).isEqualTo("MEDIUM");
        TestBase.getSoftAssertions().assertThat(response.getPlanetSize()).isEqualTo("MEDIUM");
        TestBase.getSoftAssertions().assertThat(response.getAiPresence()).isEqualTo("EVERYWHERE");
    }
}
