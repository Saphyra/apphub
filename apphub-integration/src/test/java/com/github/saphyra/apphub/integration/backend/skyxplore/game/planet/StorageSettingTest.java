package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreStorageSettingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetLocationResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.StorageSettingModel;
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

    @Test(groups = {"be", "skyxplore"})
    public void storageSettingCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId1, userId1))
            .get(accessTokenId1);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId1);

        create_validation(accessTokenId1, planet);
        StorageSettingModel createModel = createForCrud(accessTokenId1, planet);
        get(accessTokenId1, planet, createModel);
        create_alreadyExists(accessTokenId1, planet);
        UUID storageSettingId = edit_validation(accessTokenId1, planet);
        edit(accessTokenId1, planet, createModel, storageSettingId);
        delete(accessTokenId1, planet, storageSettingId);
    }

    private void create_validation(UUID accessTokenId1, PlanetLocationResponse planet) {
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(null).build(), "priority", "must not be null");
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(0).build(), "priority", "too low");
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(11).build(), "priority", "too high");
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId(" ").build(), "dataId", "must not be null or blank");
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        create_runValidationTest(accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
    }

    private static StorageSettingModel createForCrud(UUID accessTokenId1, PlanetLocationResponse planet) {
        StorageSettingModel createModel = StorageSettingModel.valid();
        StorageSettingModel created = SkyXploreStorageSettingActions.createStorageSetting(accessTokenId1, planet.getPlanetId(), createModel)
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(createModel.getDataId()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("StorageSetting is not created."));
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(createModel.getTargetAmount());
        assertThat(created.getPriority()).isEqualTo(createModel.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();
        return createModel;
    }

    private static void get(UUID accessTokenId1, PlanetLocationResponse planet, StorageSettingModel createModel) {
        StorageSettingModel created;
        List<StorageSettingModel> createModels = SkyXploreStorageSettingActions.getStorageSettings(accessTokenId1, planet.getPlanetId())
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

    private void create_alreadyExists(UUID accessTokenId1, PlanetLocationResponse planet) {
        create_runValidationTest(409, ErrorCode.ALREADY_EXISTS, accessTokenId1, planet.getPlanetId(), StorageSettingModel.valid());
    }

    private UUID edit_validation(UUID accessTokenId1, PlanetLocationResponse planet) {
        UUID storageSettingId = SkyXploreStorageSettingActions.getStorageSettings(accessTokenId1, planet.getPlanetId())
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_ORE))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("StorageSetting not found."))
            .getStorageSettingId();

        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(null).build(), "priority", "must not be null");
        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(0).build(), "priority", "too low");
        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(11).build(), "priority", "too high");
        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().dataId(" ").build(), "dataId", "must not be null or blank");
        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        edit_runValidationTest(accessTokenId1, StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
        return storageSettingId;
    }

    private static void edit(UUID accessTokenId1, PlanetLocationResponse planet, StorageSettingModel createModel, UUID storageSettingId) {
        StorageSettingModel editModel = StorageSettingModel.builder()
            .storageSettingId(storageSettingId)
            .targetAmount(325)
            .priority(2)
            .dataId(createModel.getDataId())
            .build();
        StorageSettingModel edited = SkyXploreStorageSettingActions.editStorageSetting(accessTokenId1, editModel)
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getStorageSettingId().equals(storageSettingId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Edited StorageSetting not found."));
        assertThat(edited.getDataId()).isEqualTo(editModel.getDataId());
        assertThat(edited.getTargetAmount()).isEqualTo(editModel.getTargetAmount());
        assertThat(edited.getPriority()).isEqualTo(editModel.getPriority());
        assertThat(edited.getStorageSettingId()).isEqualTo(editModel.getStorageSettingId());

        List<StorageSettingModel> editModels = SkyXploreStorageSettingActions.getStorageSettings(accessTokenId1, planet.getPlanetId())
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

    private static void delete(UUID accessTokenId1, PlanetLocationResponse planet, UUID storageSettingId) {
        SkyXploreStorageSettingActions.deleteStorageSetting(accessTokenId1, storageSettingId);
        assertThat(SkyXploreStorageSettingActions.getStorageSettings(accessTokenId1, planet.getPlanetId())).hasSize(1);

        ApphubWsClient.cleanUpConnections();
    }

    @Test(groups = {"be", "skyxplore"})
    public void produceResourcesForStorageSetting() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel);
        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());

        ApphubWsClient wsClient = SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId))
            .get(accessTokenId);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId);

        StorageSettingModel createModel = create(accessTokenId, planet);
        checkStorageReserved(accessTokenId, wsClient, planet);
        checkResourceProduced(accessTokenId, planet, createModel);
    }

    private static StorageSettingModel create(UUID accessTokenId, PlanetLocationResponse planet) {
        StorageSettingModel createModel = StorageSettingModel.valid();
        StorageSettingModel created = SkyXploreStorageSettingActions.createStorageSetting(accessTokenId, planet.getPlanetId(), createModel)
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(createModel.getDataId()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("StorageSetting was not created."));
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        return createModel;
    }

    private static void checkStorageReserved(UUID accessTokenId, ApphubWsClient wsClient, PlanetLocationResponse planet) {
        wsClient.clearMessages();
        SkyXploreGameActions.setPaused(accessTokenId, false);

        wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planet.getPlanetId()).getStorage().getBulk().getReservedStorageAmount() > 0)
            .assertTrue("Storage not reserved.");
    }

    private static void checkResourceProduced(UUID accessTokenId, PlanetLocationResponse planet, StorageSettingModel createModel) {
        AwaitilityWrapper.create(60, 10)
            .until(() -> SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planet.getPlanetId()).getStorage().getBulk().getActualResourceAmount() == createModel.getTargetAmount() + 100)
            .assertTrue("Resource not produced.");
    }

    private void create_runValidationTest(UUID accessTokenId, UUID planetId, StorageSettingModel model, String key, String value) {
        ErrorResponse errorResponse = create_runValidationTest(400, ErrorCode.INVALID_PARAM, accessTokenId, planetId, model);

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }

    private ErrorResponse create_runValidationTest(int status, ErrorCode errorCode, UUID accessTokenId, UUID planetId, StorageSettingModel model) {
        Response response = SkyXploreStorageSettingActions.getCreateStorageSettingResponse(accessTokenId, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(status);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());

        return errorResponse;
    }

    private void edit_runValidationTest(UUID accessTokenId, StorageSettingModel model, String key, String value) {
        Response response = SkyXploreStorageSettingActions.getEditStorageSettingResponse(accessTokenId, model);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }
}
