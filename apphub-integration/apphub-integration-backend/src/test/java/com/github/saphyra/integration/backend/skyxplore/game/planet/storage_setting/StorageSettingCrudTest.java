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
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
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

public class StorageSettingCrudTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider")
    public void storageSettingCrud(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXplorePlanetActions.getPopulatedPlanet(language, accessTokenId1);

        //Create - Validation
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(null).build(), "priority", "must not be null");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(0).build(), "priority", "too low");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(11).build(), "priority", "too high");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId(" ").build(), "dataId", "must not be blank");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().batchSize(null).build(), "batchSize", "must not be null");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().batchSize(0).build(), "batchSize", "too low");

        //Create
        StorageSettingModel createModel = StorageSettingModel.valid();
        SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId1, planet.getPlanetId(), createModel);

        //Get
        List<StorageSettingModel> createModels = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings();

        assertThat(createModels).hasSize(1);
        StorageSettingModel created = createModels.get(0);
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(createModel.getTargetAmount());
        assertThat(created.getBatchSize()).isEqualTo(createModel.getBatchSize());
        assertThat(created.getPriority()).isEqualTo(createModel.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();

        //Create - Already exists
        create_runValidationTest(409, ErrorCode.ALREADY_EXISTS, language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid());

        //Edit - Validation
        UUID storageSettingId = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings()
            .get(0)
            .getStorageSettingId();

        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().priority(null).build(), "priority", "must not be null");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().priority(0).build(), "priority", "too low");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().priority(11).build(), "priority", "too high");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().dataId(" ").build(), "dataId", "must not be blank");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().batchSize(null).build(), "batchSize", "must not be null");
        edit_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid(storageSettingId).toBuilder().batchSize(0).build(), "batchSize", "too low");

        //Edit
        StorageSettingModel editModel = StorageSettingModel.builder()
            .storageSettingId(storageSettingId)
            .batchSize(132)
            .targetAmount(325)
            .priority(2)
            .dataId(createModel.getDataId())
            .build();
        Response response = SkyXploreStorageSettingActions.getEditStorageSettingResponse(language, accessTokenId1, planet.getPlanetId(), editModel);
        assertThat(response.getStatusCode()).isEqualTo(200);
        List<StorageSettingModel> editModels = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings();
        assertThat(editModels).hasSize(1);
        StorageSettingModel edited = editModels.get(0);
        assertThat(edited.getDataId()).isEqualTo(editModel.getDataId());
        assertThat(edited.getTargetAmount()).isEqualTo(editModel.getTargetAmount());
        assertThat(edited.getBatchSize()).isEqualTo(editModel.getBatchSize());
        assertThat(edited.getPriority()).isEqualTo(editModel.getPriority());
        assertThat(edited.getStorageSettingId()).isEqualTo(editModel.getStorageSettingId());

        //Delete
        SkyXploreStorageSettingActions.deleteStorageSetting(language, accessTokenId1, planet.getPlanetId(), storageSettingId);
        StorageSettingsResponse storageSettingsResponse = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId());
        assertThat(storageSettingsResponse.getCurrentSettings()).isEmpty();
    }

    private void create_runValidationTest(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model, String key, String value) {
        ErrorResponse errorResponse = create_runValidationTest(400, ErrorCode.INVALID_PARAM, language, accessTokenId, planetId, model);

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }

    private ErrorResponse create_runValidationTest(int status, ErrorCode errorCode, Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        Response response = SkyXploreStorageSettingActions.getCreateStorageSettingResponse(language, accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(status);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.valueOf(errorCode.name())));

        return errorResponse;
    }

    private void edit_runValidationTest(Language language, UUID accessTokenId, UUID planetId, StorageSettingModel model, String key, String value) {
        Response response = SkyXploreStorageSettingActions.getEditStorageSettingResponse(language, accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.valueOf(ErrorCode.INVALID_PARAM.name())));

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }
}
