package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.building;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
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

public class UpgradeBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void upgradeBuilding() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(accessTokenId, planetId);

        buildingAtMaxLevel(accessTokenId, planetId);
        UUID upgradableBuildingId = findBuilding(accessTokenId, planetId, Constants.DATA_ID_SOLAR_PANEL);
        upgradeBuilding(accessTokenId, planetWsClient, planetId, upgradableBuildingId);
        constructionAlreadyInProgress(accessTokenId, planetId, upgradableBuildingId);
        cancel(accessTokenId, planetWsClient, planetId, upgradableBuildingId);
    }

    private void buildingAtMaxLevel(UUID accessTokenId, UUID planetId) {
        UUID maxLevelBuildingId = findBuilding(accessTokenId, planetId, Constants.DATA_ID_WATER_PUMP);

        Response maxLevelResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(accessTokenId, planetId, maxLevelBuildingId);

        ResponseValidator.verifyForbiddenOperation(maxLevelResponse);
    }

    private static void upgradeBuilding(UUID accessTokenId, ApphubWsClient planetWsClient, UUID planetId, UUID upgradableBuildingId) {
        planetWsClient.clearMessages();
        SkyXploreBuildingActions.upgradeBuilding(accessTokenId, planetId, upgradableBuildingId);

        PlanetOverviewResponse planetOverviewResponse = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> SkyXplorePlanetActions.findSurfaceByBuildingId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), upgradableBuildingId)
                    .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding().getConstruction()))
                    .isPresent()
            )
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED + " event did not arrive."))
            .getPayloadAs(PlanetOverviewResponse.class);

        SurfaceResponse modifiedSurface = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), upgradableBuildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding().getConstruction().getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = planetOverviewResponse.getStorage();

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(10);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(10);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(10);

        QueueResponse queueItemModifiedEvent = planetOverviewResponse.getQueue()
            .stream()
            .filter(queueResponse -> queueResponse.getType().equals(Constants.QUEUE_TYPE_CONSTRUCTION))
            .findAny()
            .orElseThrow(() -> new RuntimeException("QueueItem not found."));

        UUID constructionId = modifiedSurface.getBuilding().getConstruction().getConstructionId();
        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(constructionId);
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("currentLevel", 1);
    }

    private static void constructionAlreadyInProgress(UUID accessTokenId, UUID planetId, UUID upgradableBuildingId) {
        Response inProgressResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(accessTokenId, planetId, upgradableBuildingId);

        ResponseValidator.verifyErrorResponse(inProgressResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private void cancel(UUID accessTokenId, ApphubWsClient planetWsClient, UUID planetId, UUID upgradableBuildingId) {
        planetWsClient.clearMessages();
        SkyXploreBuildingActions.cancelConstruction(accessTokenId, planetId, upgradableBuildingId);

        PlanetOverviewResponse planetOverviewResponse = planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> SkyXplorePlanetActions.findSurfaceByBuildingId(webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(), upgradableBuildingId).filter(surfaceResponse -> isNull(surfaceResponse.getBuilding().getConstruction())).isPresent()
            )
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED + " event did not arrive."))
            .getPayloadAs(PlanetOverviewResponse.class);

        SurfaceResponse modifiedSurface = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), upgradableBuildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding().getConstruction()).isNull();

        assertThat(findByBuildingId(accessTokenId, planetId, upgradableBuildingId).getConstruction()).isNull();

        assertThat(planetOverviewResponse.getQueue()).isEmpty();
    }

    private SurfaceBuildingResponse findByBuildingId(UUID accessTokenId, UUID planetId, UUID buildingId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .map(SurfaceResponse::getBuilding)
            .filter(building -> !isNull(building))
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getBuildingId().equals(buildingId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building not found by buildingId " + buildingId));
    }

    private UUID findBuilding(UUID accessTokenId, UUID planetId, String dataId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .map(SurfaceResponse::getBuilding)
            .filter(building -> !isNull(building))
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getDataId().equals(dataId))
            .map(SurfaceBuildingResponse::getBuildingId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building " + dataId + " not found on planet " + planetId));
    }
}
