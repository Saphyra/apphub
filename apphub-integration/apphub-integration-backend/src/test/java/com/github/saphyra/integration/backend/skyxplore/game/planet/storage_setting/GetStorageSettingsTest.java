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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GetStorageSettingsTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test
    public void getStorageSettings() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        List<String> resourceList = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getAvailableResources();

        StorageSettingModel model = StorageSettingModel.valid();
        SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId1, planet.getPlanetId(), model);

        StorageSettingsResponse response = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId());

        assertThat(response.getCurrentSettings()).hasSize(1);
        StorageSettingModel created = response.getCurrentSettings().get(0);
        assertThat(created.getDataId()).isEqualTo(model.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(model.getTargetAmount());
        assertThat(created.getBatchSize()).isEqualTo(model.getBatchSize());
        assertThat(created.getPriority()).isEqualTo(model.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();

        assertThat(response.getAvailableResources()).hasSize(resourceList.size() - 1);
        assertThat(response.getAvailableResources()).doesNotContain(model.getDataId());
    }
}
