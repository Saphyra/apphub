package com.github.saphyra.integration.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.GameSettingsResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetGameSettingsTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = "skyxplore")
    public void getGameSettings() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);

        SkyXploreLobbyActions.createLobby(language, accessTokenId1, GAME_NAME);

        GameSettingsResponse response = SkyXploreLobbyActions.getGameSettings(language, accessTokenId1);

        assertThat(response.getUniverseSize()).isEqualTo("SMALL");
        assertThat(response.getSystemAmount()).isEqualTo("MEDIUM");
        assertThat(response.getSystemSize()).isEqualTo("MEDIUM");
        assertThat(response.getPlanetSize()).isEqualTo("MEDIUM");
        assertThat(response.getAiPresence()).isEqualTo("EVERYWHERE");
    }
}
