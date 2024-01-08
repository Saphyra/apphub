package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.building;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.WsActions;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BuildNewBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void constructNewBuilding(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(accessTokenId, planetId);

        WsActions.sendSkyXplorePageOpenedMessage(gameWsClient, Constants.PAGE_TYPE_PLANET, planetId);

        UUID emptyDesertSurfaceId = invalidDataId(language, accessTokenId, planetId);
        buildingAlreadyExists(language, accessTokenId, planetId);
        incompatibleSurfaceType(language, accessTokenId, planetId);
        BiWrapper<SurfaceResponse, UUID> buildResult = build(language, accessTokenId, planetId, emptyDesertSurfaceId, planetWsClient);
        SurfaceResponse modifiedSurface = buildResult.getEntity1();
        UUID constructionId = buildResult.getEntity2();
        cancel(language, accessTokenId, planetWsClient, planetId, emptyDesertSurfaceId, modifiedSurface, constructionId);
    }

    private static UUID invalidDataId(Language language, UUID accessTokenId, UUID planetId) {
        UUID emptyDesertSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        Response invalidDataIdResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, emptyDesertSurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(language, invalidDataIdResponse, "dataId", "invalid value");
        return emptyDesertSurfaceId;
    }

    private void buildingAlreadyExists(Language language, UUID accessTokenId, UUID planetId) {
        UUID occupiedDesertSurfaceId = findOccupiedDesert(accessTokenId, planetId);

        Response buildingAlreadyExistsResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, occupiedDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyErrorResponse(language, buildingAlreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static void incompatibleSurfaceType(Language language, UUID accessTokenId, UUID planetId) {
        UUID emptyLakeSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);

        Response incompatibleSurfaceTypeResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, emptyLakeSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyForbiddenOperation(language, incompatibleSurfaceTypeResponse);
    }

    private BiWrapper<SurfaceResponse, UUID> build(Language language, UUID accessTokenId, UUID planetId, UUID emptyDesertSurfaceId, ApphubWsClient planetWsClient) {
        planetWsClient.clearMessages();

        SkyXploreBuildingActions.constructNewBuilding(language, accessTokenId, planetId, emptyDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        PlanetOverviewResponse updatedPlanetOverview = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> findBySurfaceId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), emptyDesertSurfaceId).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("Planet modified event did not arrive."))
            .getPayloadAs(PlanetOverviewResponse.class);

        SurfaceResponse modifiedSurface = findBySurfaceId(updatedPlanetOverview.getSurfaces(), emptyDesertSurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding().getDataId()).isEqualTo(Constants.DATA_ID_SOLAR_PANEL);
        assertThat(modifiedSurface.getBuilding().getLevel()).isEqualTo(0);
        assertThat(modifiedSurface.getBuilding().getConstruction().getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = SkyXplorePlanetActions.getStorageOverview(accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(1);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(1);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(1);

        QueueResponse queueItem = updatedPlanetOverview.getQueue()
            .stream()
            .filter(queueResponse -> Constants.DATA_ID_SOLAR_PANEL.equals(queueResponse.getData().get("dataId")))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Corresponding queueItem not found."));

        UUID constructionId = modifiedSurface.getBuilding().getConstruction().getConstructionId();
        assertThat(queueItem.getItemId()).isEqualTo(constructionId);
        assertThat(queueItem.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueItem.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
        assertThat(queueItem.getData()).containsEntry("currentLevel", 0);

        assertThat(updatedPlanetOverview.getStorage()).isNotNull();

        //TODO restore when buildingDetails message sender is created
        //PlanetBuildingDetailsValidator.verifyBuildingDetails(updatedPlanetOverview.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 2, 1);

        return new BiWrapper<>(modifiedSurface, constructionId);
    }

    private Optional<SurfaceResponse> findBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        assertThat(surfaces).isNotNull();

        return surfaces.stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findAny();
    }

    private void cancel(Language language, UUID accessTokenId, ApphubWsClient planetWsClient, UUID planetId, UUID emptyDesertSurfaceId, SurfaceResponse modifiedSurface, UUID constructionId) {
        planetWsClient.clearMessages();

        SkyXploreBuildingActions.cancelConstruction(language, accessTokenId, planetId, modifiedSurface.getBuilding().getBuildingId());

        PlanetOverviewResponse updatedPlanetOverview = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> findBySurfaceId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), emptyDesertSurfaceId).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("Planet modified event did not arrive."))
            .getPayloadAs(PlanetOverviewResponse.class);

        modifiedSurface = findBySurfaceId(updatedPlanetOverview.getSurfaces(), emptyDesertSurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding()).isNull();

        PlanetStorageResponse storageResponse = SkyXplorePlanetActions.getStorageOverview(accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(0);

        assertThat(findBySurfaceId(accessTokenId, planetId, emptyDesertSurfaceId).getBuilding()).isNull();

        assertThat(updatedPlanetOverview.getQueue()).isEmpty();

        assertThat(updatedPlanetOverview.getStorage()).isNotNull();

        //TODO restore when buildingDetails message sender is created
        //PlanetBuildingDetailsValidator.verifyBuildingDetails(updatedPlanetOverview.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 1, 1);
    }

    @Test(groups = {"be", "skyxplore"})
    void finishConstruction() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(accessTokenId, planetId);

        WsActions.sendSkyXplorePageOpenedMessage(gameWsClient, Constants.PAGE_TYPE_PLANET, planetId);

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_FOREST);

        //Start construction
        planetWsClient.clearMessages();
        SkyXploreBuildingActions.constructNewBuilding(language, accessTokenId, planetId, surfaceId, Constants.DATA_ID_CAMP);

        PlanetOverviewResponse updatedPlanetOverview = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> findBySurfaceId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), surfaceId).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("Planet modified event did not arrive."))
            .getPayloadAs(PlanetOverviewResponse.class);

        UUID constructionId = findBySurfaceId(updatedPlanetOverview.getSurfaces(), surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."))
            .getBuilding()
            .getConstruction()
            .getConstructionId();

        //Resume game
        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(language, accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        updatedPlanetOverview = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                180,
                webSocketEvent -> Optional.ofNullable(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getQueue()).filter(List::isEmpty).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("No planetModified event with empty queue"))
            .getPayloadAs(PlanetOverviewResponse.class);


        //TODO restore when buildingDetails message sender is created
        //PlanetBuildingDetailsValidator.verifyBuildingDetails(updatedPlanetOverview.getBuildings(), Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP, 2, 2);
    }

    private SurfaceResponse findBySurfaceId(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Surface not found by surfaceId " + surfaceId));
    }


    private UUID findOccupiedDesert(UUID accessTokenId, UUID planetId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(Constants.SURFACE_TYPE_DESERT))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Occupied Desert not found on planet " + planetId));
    }
}
