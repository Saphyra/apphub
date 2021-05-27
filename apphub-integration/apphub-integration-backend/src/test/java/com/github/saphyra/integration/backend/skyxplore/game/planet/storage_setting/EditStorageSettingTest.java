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
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EditStorageSettingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "localeDataProvider", groups = "skyxplore")
    public void validation(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        StorageSettingModel original = StorageSettingModel.valid();
        SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId1, planet.getPlanetId(), original);

        UUID storageSettingId = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings()
            .get(0)
            .getStorageSettingId();

        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().priority(null).build(), "priority", "must not be null");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().priority(0).build(), "priority", "too low");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().priority(11).build(), "priority", "too high");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().dataId(" ").build(), "dataId", "must not be blank");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().batchSize(null).build(), "batchSize", "must not be null");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().batchSize(0).build(), "batchSize", "too low");
    }

    private void runValidationTest(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model, String key, String value) {
        Response response = SkyXploreStorageSettingActions.getEditStorageSettingResponse(language, accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.valueOf(ErrorCode.INVALID_PARAM.name())));

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }
}
