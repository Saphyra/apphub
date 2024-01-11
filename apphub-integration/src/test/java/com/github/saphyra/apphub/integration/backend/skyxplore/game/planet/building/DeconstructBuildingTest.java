package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.building;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.PlanetBuildingDetailsValidator;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.DeconstructionResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceBuildingResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void deconstructBuilding() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(accessTokenId, planetId);

        storageInUse(accessTokenId, planetId);
        UUID emptyDesertSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);
        buildingUnderConstruction(accessTokenId, planetWsClient, emptyDesertSurfaceId, planetId);
        gameWsClient.clearMessages();
        UUID buildingId = findBuilding(accessTokenId, planetId, Constants.DATA_ID_BATTERY);
        deconstructBuilding(accessTokenId, planetWsClient, planetId, buildingId);
        cancelDeconstruction(accessTokenId, planetWsClient, planetId, buildingId);
    }

    private void storageInUse(UUID accessTokenId, UUID planetId) {
        UUID buildingId = findBuilding(accessTokenId, planetId, Constants.DATA_ID_DEPOT);
        Response response = SkyXploreBuildingActions.getDeconstructBuildingResponse(accessTokenId, planetId, buildingId);

        ResponseValidator.verifyErrorResponse(response, 400, ErrorCode.SKYXPLORE_STORAGE_USED);
    }

    private static void buildingUnderConstruction(UUID accessTokenId, ApphubWsClient planetWsClient, UUID emptyDesertSurfaceId, UUID planetId) {
        planetWsClient.clearMessages();
        SkyXploreBuildingActions.constructNewBuilding(accessTokenId, planetId, emptyDesertSurfaceId, Constants.DATA_ID_WATER_PUMP);
        UUID buildingId = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> SkyXplorePlanetActions.findSurfaceBySurfaceId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), emptyDesertSurfaceId).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("PlanetModified event not arrived"))
            .getPayloadAs(PlanetOverviewResponse.class)
            .getSurfaces()
            .stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(emptyDesertSurfaceId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Surface not found"))
            .getBuilding()
            .getBuildingId();

        Response constructionInProgressResponse = SkyXploreBuildingActions.getDeconstructBuildingResponse(accessTokenId, planetId, buildingId);

        ResponseValidator.verifyForbiddenOperation(constructionInProgressResponse);
    }

    private static void deconstructBuilding(UUID accessTokenId, ApphubWsClient planetWsClient, UUID planetId, UUID buildingId) {
        planetWsClient.clearMessages();

        SkyXploreBuildingActions.deconstructBuilding(accessTokenId, planetId, buildingId);

        PlanetOverviewResponse planetOverviewResponse = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> SkyXplorePlanetActions.findSurfaceByBuildingId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), buildingId).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("PlanetModified event not arrived"))
            .getPayloadAs(PlanetOverviewResponse.class);

        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), buildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        DeconstructionResponse deconstruction = surfaceResponse.getBuilding()
            .getDeconstruction();
        assertThat(deconstruction).isNotNull();

        QueueResponse queueItem = planetOverviewResponse.getQueue()
            .stream()
            .filter(queueResponse -> Constants.DATA_ID_BATTERY.equals(queueResponse.getData().get("dataId")))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Corresponding queueItem not found."));

        assertThat(queueItem.getItemId()).isEqualTo(deconstruction.getDeconstructionId());
        assertThat(queueItem.getType()).isEqualTo(Constants.QUEUE_TYPE_DECONSTRUCTION);
        assertThat(queueItem.getData()).containsEntry("dataId", Constants.DATA_ID_BATTERY);

        //TODO restore when buildingDetails message sender is created
        //PlanetBuildingDetailsValidator.verifyBuildingDetails(updatedPlanetOverview.getBuildings(), Constants.SURFACE_TYPE_CONCRETE, Constants.DATA_ID_BATTERY, 1, 0);

        PlanetStorageResponse storageResponse = SkyXplorePlanetActions.getStorageOverview(accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getCapacity()).isEqualTo(0);
    }

    private static void cancelDeconstruction(UUID accessTokenId, ApphubWsClient planetWsClient, UUID planetId, UUID buildingId) {
        planetWsClient.clearMessages();

        SkyXploreBuildingActions.cancelDeconstruction(accessTokenId, planetId, buildingId);

        PlanetOverviewResponse planetOverviewResponse = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> SkyXplorePlanetActions.findSurfaceByBuildingId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), buildingId).isPresent()
            )
            .orElseThrow(() -> new RuntimeException("PlanetModified event not arrived"))
            .getPayloadAs(PlanetOverviewResponse.class);

        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), buildingId)
            .orElseThrow(() -> new RuntimeException("SurfaceResponse not found"));

        assertThat(surfaceResponse.getBuilding().getDeconstruction()).isNull();

        assertThat(planetOverviewResponse.getQueue()).extracting(QueueResponse::getType).doesNotContain(Constants.QUEUE_TYPE_DECONSTRUCTION);

        //TODO restore when buildingDetails message sender is created
        //PlanetBuildingDetailsValidator.verifyBuildingDetails(updatedPlanetOverview.getBuildings(), Constants.SURFACE_TYPE_CONCRETE, Constants.DATA_ID_BATTERY, 1, 1);
    }

    @Test(groups = {"be", "skyxplore"})
    public void finishDeconstruction() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(accessTokenId, planetId);

        UUID buildingId = findBuilding(accessTokenId, planetId, Constants.DATA_ID_SOLAR_PANEL);

        planetWsClient.clearMessages();
        SkyXploreBuildingActions.deconstructBuilding(accessTokenId, planetId, buildingId);

        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.findSurfaceByBuildingId(SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId), buildingId).isEmpty())
            .assertTrue("Building is not deconstructed.");

        PlanetBuildingDetailsValidator.verifyBuildingDetails(SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId).getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 0, 0);
    }

    private UUID findBuilding(UUID accessTokenId, UUID planetId, String dataId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .map(SurfaceResponse::getBuilding)
            .filter(building -> !isNull(building))
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getDataId().equals(dataId))
            .map(SurfaceBuildingResponse::getBuildingId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(dataId + " building not found on planet " + planetId));
    }
}
