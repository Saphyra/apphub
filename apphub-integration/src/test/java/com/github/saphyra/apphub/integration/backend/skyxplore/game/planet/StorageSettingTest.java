package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetStorageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreStorageSettingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingsResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StorageSettingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void storageSettingCrud(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId1);

        create_validation(language, accessTokenId1, planet);
        StorageSettingModel createModel = createForCrud(language, accessTokenId1, planet);
        get(language, accessTokenId1, planet, createModel);
        create_alreadyExists(language, accessTokenId1, planet);
        UUID storageSettingId = edit_validation(language, accessTokenId1, planet);
        edit(language, accessTokenId1, planet, createModel, storageSettingId);
        delete(language, accessTokenId1, planet, storageSettingId);
    }

    private void create_validation(Language language, UUID accessTokenId1, PlanetLocationResponse planet) {
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(null).build(), "priority", "must not be null");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(0).build(), "priority", "too low");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(11).build(), "priority", "too high");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId(" ").build(), "dataId", "must not be null or blank");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        create_runValidationTest(language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
    }

    private static StorageSettingModel createForCrud(Language language, UUID accessTokenId1, PlanetLocationResponse planet) {
        StorageSettingModel createModel = StorageSettingModel.valid();
        StorageSettingModel created = SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId1, planet.getPlanetId(), createModel);
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(createModel.getTargetAmount());
        assertThat(created.getPriority()).isEqualTo(createModel.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();
        return createModel;
    }

    private static void get(Language language, UUID accessTokenId1, PlanetLocationResponse planet, StorageSettingModel createModel) {
        StorageSettingModel created;
        List<StorageSettingModel> createModels = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings()
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_ORE))
            .toList();

        assertThat(createModels).hasSize(1);
        created = createModels.get(0);
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(createModel.getTargetAmount());
        assertThat(created.getPriority()).isEqualTo(createModel.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();
    }

    private void create_alreadyExists(Language language, UUID accessTokenId1, PlanetLocationResponse planet) {
        create_runValidationTest(409, ErrorCode.ALREADY_EXISTS, language, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid());
    }

    private UUID edit_validation(Language language, UUID accessTokenId1, PlanetLocationResponse planet) {
        UUID storageSettingId = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings()
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_ORE))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("StorageSetting not found."))
            .getStorageSettingId();

        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(null).build(), "priority", "must not be null");
        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(0).build(), "priority", "too low");
        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(11).build(), "priority", "too high");
        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().dataId(" ").build(), "dataId", "must not be null or blank");
        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        edit_runValidationTest(language, accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
        return storageSettingId;
    }

    private static void edit(Language language, UUID accessTokenId1, PlanetLocationResponse planet, StorageSettingModel createModel, UUID storageSettingId) {
        StorageSettingModel editModel = StorageSettingModel.builder()
            .storageSettingId(storageSettingId)
            .targetAmount(325)
            .priority(2)
            .dataId(createModel.getDataId())
            .build();
        StorageSettingModel edited = SkyXploreStorageSettingActions.editStorageSetting(language, accessTokenId1, editModel);
        assertThat(edited.getDataId()).isEqualTo(editModel.getDataId());
        assertThat(edited.getTargetAmount()).isEqualTo(editModel.getTargetAmount());
        assertThat(edited.getPriority()).isEqualTo(editModel.getPriority());
        assertThat(edited.getStorageSettingId()).isEqualTo(editModel.getStorageSettingId());

        List<StorageSettingModel> editModels = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId())
            .getCurrentSettings()
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_ORE))
            .toList();
        assertThat(editModels).hasSize(1);
        edited = editModels.get(0);
        assertThat(edited.getDataId()).isEqualTo(editModel.getDataId());
        assertThat(edited.getTargetAmount()).isEqualTo(editModel.getTargetAmount());
        assertThat(edited.getPriority()).isEqualTo(editModel.getPriority());
        assertThat(edited.getStorageSettingId()).isEqualTo(editModel.getStorageSettingId());
    }

    private static void delete(Language language, UUID accessTokenId1, PlanetLocationResponse planet, UUID storageSettingId) {
        SkyXploreStorageSettingActions.deleteStorageSetting(language, accessTokenId1, storageSettingId);
        StorageSettingsResponse storageSettingsResponse = SkyXploreStorageSettingActions.getStorageSettings(language, accessTokenId1, planet.getPlanetId());
        assertThat(storageSettingsResponse.getCurrentSettings()).hasSize(1);

        ApphubWsClient.cleanUpConnections();
    }

    @Test(groups = {"be", "skyxplore"}, priority = Integer.MIN_VALUE)
    public void produceResourcesForStorageSetting() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, characterModel);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());

        ApphubWsClient wsClient = SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId, userId))
            .get(accessTokenId);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId);

        StorageSettingModel createModel = create(language, accessTokenId, planet);
        checkStorageReserved(language, accessTokenId, wsClient, planet);
        checkResourceProduced(language, accessTokenId, planet, createModel);
    }

    private static StorageSettingModel create(Language language, UUID accessTokenId, PlanetLocationResponse planet) {
        StorageSettingModel createModel = StorageSettingModel.valid();
        StorageSettingModel created = SkyXploreStorageSettingActions.createStorageSetting(language, accessTokenId, planet.getPlanetId(), createModel);
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        return createModel;
    }

    private static void checkStorageReserved(Language language, UUID accessTokenId, ApphubWsClient wsClient, PlanetLocationResponse planet) {
        wsClient.clearMessages();
        SkyXploreGameActions.setPaused(language, accessTokenId, false);

        wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetStorageActions.getStorageOverview(language, accessTokenId, planet.getPlanetId()).getBulk().getReservedStorageAmount() > 0)
            .assertTrue("Storage not reserved.");
    }

    private static void checkResourceProduced(Language language, UUID accessTokenId, PlanetLocationResponse planet, StorageSettingModel createModel) {
        AwaitilityWrapper.create(60, 10)
            .until(() -> SkyXplorePlanetStorageActions.getStorageOverview(language, accessTokenId, planet.getPlanetId()).getBulk().getActualResourceAmount() == createModel.getTargetAmount() + 100)
            .assertTrue("Resource not produced.");
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

    private void edit_runValidationTest(Language language, UUID accessTokenId, StorageSettingModel model, String key, String value) {
        Response response = SkyXploreStorageSettingActions.getEditStorageSettingResponse(language, accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.valueOf(ErrorCode.INVALID_PARAM.name())));

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }
}
