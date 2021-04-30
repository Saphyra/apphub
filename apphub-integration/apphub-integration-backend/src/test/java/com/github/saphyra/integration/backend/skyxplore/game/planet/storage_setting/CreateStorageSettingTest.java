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
import com.github.saphyra.apphub.integration.common.TestBase;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateStorageSettingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "localeDataProvider")
    public void validation(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(null).build(), "priority", "must not be null");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(0).build(), "priority", "too low");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(11).build(), "priority", "too high");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId(" ").build(), "dataId", "must not be blank");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(0).build(), "targetAmount", "too low");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().batchSize(null).build(), "batchSize", "must not be null");
        runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().batchSize(0).build(), "batchSize", "too low");

        SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid());
        runValidationTest(409, ErrorCode.ALREADY_EXISTS, language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid());
    }

    @Test
    public void createStorageSetting() {
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

        List<StorageSettingModel> models = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings();

        assertThat(models).hasSize(1);
        StorageSettingModel created = models.get(0);
        assertThat(created.getDataId()).isEqualTo(model.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(model.getTargetAmount());
        assertThat(created.getBatchSize()).isEqualTo(model.getBatchSize());
        assertThat(created.getPriority()).isEqualTo(model.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();
    }

    private void runValidationTest(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model, String key, String value) {
        ErrorResponse errorResponse = runValidationTest(400, ErrorCode.INVALID_PARAM, language, accessTokenId, planetId, model);

        TestBase.getSoftAssertions().assertThat(errorResponse.getParams()).containsEntry(key, value);
    }

    private ErrorResponse runValidationTest(int status, ErrorCode errorCode, Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        Response response = SkyXploreStorageSettingActions.getCreateStorageSettingResponse(language, accessTokenId, planetId, model);

        TestBase.getSoftAssertions().assertThat(response.getStatusCode()).isEqualTo(status);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        TestBase.getSoftAssertions().assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        TestBase.getSoftAssertions().assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.valueOf(errorCode.name())));

        return errorResponse;
    }
}
