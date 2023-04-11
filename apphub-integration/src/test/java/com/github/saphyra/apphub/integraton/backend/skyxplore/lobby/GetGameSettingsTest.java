package com.github.saphyra.apphub.integraton.backend.skyxplore.lobby;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.Range;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreGameSettings;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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

        SkyXploreGameSettings response = SkyXploreLobbyActions.getGameSettings(language, accessTokenId1);

        assertThat(response.getMaxPlayersPerSolarSystem()).isEqualTo(2);
        assertThat(response.getAdditionalSolarSystems()).isEqualTo(new Range<>(1, 2));
        assertThat(response.getPlanetsPerSolarSystem()).isEqualTo(new Range<>(0, 3));
        assertThat(response.getPlanetSize()).isEqualTo(new Range<>(10, 15));
    }
}
