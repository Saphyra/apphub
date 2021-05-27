package com.github.saphyra.integration.backend.skyxplore.game.planet.storage_setting;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreStorageSettingActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.StorageSettingModel;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.StorageSettingsResponse;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteStorageSettingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = "skyxplore")
    public void deleteStorageSetting() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        StorageSettingModel model = StorageSettingModel.valid();
        SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId1, planet.getPlanetId(), model);

        UUID storageSettingId = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings()
            .get(0)
            .getStorageSettingId();

        SkyXploreStorageSettingActions.deleteStorageSetting(language, accessTokenId1, planet.getPlanetId(), storageSettingId);

        StorageSettingsResponse storageSettingsResponse = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId());
        assertThat(storageSettingsResponse.getCurrentSettings()).isEmpty();
    }
}
