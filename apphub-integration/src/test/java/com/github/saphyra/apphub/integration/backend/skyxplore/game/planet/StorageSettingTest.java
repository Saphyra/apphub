package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreStorageSettingActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
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
        String accessToken1 = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken1, characterModel1);
        UUID userId1 = UserDynamoDbRepository.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken1, userId1))
            .get(accessToken1);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken1);

        create_validation(accessToken1, planet);
        StorageSettingModel createModel = createForCrud(accessToken1, planet);
        get(accessToken1, planet, createModel);
        create_alreadyExists(accessToken1, planet);
        UUID storageSettingId = edit_validation(accessToken1, planet);
        edit(accessToken1, planet, createModel, storageSettingId);
        delete(accessToken1, planet, storageSettingId);
    }

    private void create_validation(String accessToken1, PlanetLocationResponse planet) {
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(null).build(), "priority", "must not be null");
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(0).build(), "priority", "too low");
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().priority(11).build(), "priority", "too high");
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId(" ").build(), "dataId", "must not be null or blank");
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        create_runValidationTest(accessToken1, planet.getPlanetId(), StorageSettingModel.valid().toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
    }

    private static StorageSettingModel createForCrud(String accessToken1, PlanetLocationResponse planet) {
        StorageSettingModel createModel = StorageSettingModel.valid();
        StorageSettingModel created = SkyXploreStorageSettingActions.createStorageSetting(getServerPort(), accessToken1, planet.getPlanetId(), createModel)
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

    private static void get(String accessToken1, PlanetLocationResponse planet, StorageSettingModel createModel) {
        StorageSettingModel created;
        List<StorageSettingModel> createModels = SkyXploreStorageSettingActions.getStorageSettings(getServerPort(), accessToken1, planet.getPlanetId())
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_STEEL_INGOT))
            .toList();

        assertThat(createModels).hasSize(1);
        created = createModels.get(0);
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        assertThat(created.getTargetAmount()).isEqualTo(createModel.getTargetAmount());
        assertThat(created.getPriority()).isEqualTo(createModel.getPriority());
        assertThat(created.getStorageSettingId()).isNotNull();
    }

    private void create_alreadyExists(String accessToken1, PlanetLocationResponse planet) {
        create_runValidationTest(409, ErrorCode.ALREADY_EXISTS, accessToken1, planet.getPlanetId(), StorageSettingModel.valid());
    }

    private UUID edit_validation(String accessToken1, PlanetLocationResponse planet) {
        UUID storageSettingId = SkyXploreStorageSettingActions.getStorageSettings(getServerPort(), accessToken1, planet.getPlanetId())
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_STEEL_INGOT))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("StorageSetting not found."))
            .getStorageSettingId();

        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(null).build(), "priority", "must not be null");
        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(0).build(), "priority", "too low");
        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().priority(11).build(), "priority", "too high");
        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().dataId(" ").build(), "dataId", "must not be null or blank");
        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().dataId("asd").build(), "dataId", "unknown resource");
        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(null).build(), "targetAmount", "must not be null");
        edit_runValidationTest(accessToken1, StorageSettingModel.valid(storageSettingId).toBuilder().targetAmount(-1).build(), "targetAmount", "too low");
        return storageSettingId;
    }

    private static void edit(String accessToken1, PlanetLocationResponse planet, StorageSettingModel createModel, UUID storageSettingId) {
        StorageSettingModel editModel = StorageSettingModel.builder()
            .storageSettingId(storageSettingId)
            .targetAmount(325)
            .priority(2)
            .dataId(createModel.getDataId())
            .build();
        StorageSettingModel edited = SkyXploreStorageSettingActions.editStorageSetting(getServerPort(), accessToken1, editModel)
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getStorageSettingId().equals(storageSettingId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Edited StorageSetting not found."));
        assertThat(edited.getDataId()).isEqualTo(editModel.getDataId());
        assertThat(edited.getTargetAmount()).isEqualTo(editModel.getTargetAmount());
        assertThat(edited.getPriority()).isEqualTo(editModel.getPriority());
        assertThat(edited.getStorageSettingId()).isEqualTo(editModel.getStorageSettingId());

        List<StorageSettingModel> editModels = SkyXploreStorageSettingActions.getStorageSettings(getServerPort(), accessToken1, planet.getPlanetId())
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(Constants.DATA_ID_STEEL_INGOT))
            .toList();
        assertThat(editModels).hasSize(1);
        edited = editModels.get(0);
        assertThat(edited.getDataId()).isEqualTo(editModel.getDataId());
        assertThat(edited.getTargetAmount()).isEqualTo(editModel.getTargetAmount());
        assertThat(edited.getPriority()).isEqualTo(editModel.getPriority());
        assertThat(edited.getStorageSettingId()).isEqualTo(editModel.getStorageSettingId());
    }

    private static void delete(String accessToken1, PlanetLocationResponse planet, UUID storageSettingId) {
        SkyXploreStorageSettingActions.deleteStorageSetting(getServerPort(), accessToken1, storageSettingId);
        assertThat(SkyXploreStorageSettingActions.getStorageSettings(getServerPort(), accessToken1, planet.getPlanetId())).hasSize(1);

        ApphubWsClient.cleanUpConnections();
    }

    @Test(groups = {"be", "skyxplore"})
    public void produceResourcesForStorageSetting() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel = SkyXploreCharacterModel.valid();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, characterModel);
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());

        ApphubWsClient wsClient = SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken, userId))
            .get(accessToken);

        PlanetLocationResponse planet = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken);

        StorageSettingModel createModel = create(accessToken, planet);
        checkStorageReserved(accessToken, wsClient, planet);
        checkResourceProduced(accessToken, planet, createModel);
    }

    private static StorageSettingModel create(String accessToken, PlanetLocationResponse planet) {
        StorageSettingModel createModel = StorageSettingModel.valid();
        StorageSettingModel created = SkyXploreStorageSettingActions.createStorageSetting(getServerPort(), accessToken, planet.getPlanetId(), createModel)
            .stream()
            .filter(storageSettingModel -> storageSettingModel.getDataId().equals(createModel.getDataId()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("StorageSetting was not created."));
        assertThat(created.getDataId()).isEqualTo(createModel.getDataId());
        return createModel;
    }

    private static void checkStorageReserved(String accessToken, ApphubWsClient wsClient, PlanetLocationResponse planet) {
        wsClient.clearMessages();
        Integer serverPort = getServerPort();
        SkyXploreGameActions.setPaused(serverPort, accessToken, false);

        wsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getPlanetOverview(serverPort, accessToken, planet.getPlanetId()).getStorage().getBulk().getReservedStorageAmount() > 0)
            .assertTrue("Storage not reserved.");
    }

    private static void checkResourceProduced(String accessToken, PlanetLocationResponse planet, StorageSettingModel createModel) {
        Integer serverPort = getServerPort();
        AwaitilityWrapper.create(120, 10)
            .until(() -> SkyXplorePlanetActions.getPlanetOverview(serverPort, accessToken, planet.getPlanetId()).getStorage().getBulk().getActualResourceAmount() == createModel.getTargetAmount() + 100)
            .assertTrue("Resource not produced.");
    }

    private void create_runValidationTest(String accessToken, UUID planetId, StorageSettingModel model, String key, String value) {
        ErrorResponse errorResponse = create_runValidationTest(400, ErrorCode.INVALID_PARAM, accessToken, planetId, model);

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }

    private ErrorResponse create_runValidationTest(int status, ErrorCode errorCode, String accessToken, UUID planetId, StorageSettingModel model) {
        Response response = SkyXploreStorageSettingActions.getCreateStorageSettingResponse(getServerPort(), accessToken, planetId, model);

        assertThat(response.getStatusCode()).isEqualTo(status);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(errorCode.name());

        return errorResponse;
    }

    private void edit_runValidationTest(String accessToken, StorageSettingModel model, String key, String value) {
        Response response = SkyXploreStorageSettingActions.getEditStorageSettingResponse(getServerPort(), accessToken, model);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());

        assertThat(errorResponse.getParams()).containsEntry(key, value);
    }
}
