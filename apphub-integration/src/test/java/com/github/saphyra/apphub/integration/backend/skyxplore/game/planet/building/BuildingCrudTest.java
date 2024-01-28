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
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BuildingCrudTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void buildingCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();

        //Create and cancel construction
        UUID emptyDesertSurfaceId = invalidDataId(accessTokenId, planetId);
        buildingAlreadyExists(accessTokenId, planetId);
        incompatibleSurfaceType(accessTokenId, planetId);
        SurfaceResponse modifiedSurface = build(accessTokenId, planetId, emptyDesertSurfaceId);
        cancel(accessTokenId, planetId, emptyDesertSurfaceId, modifiedSurface);

        //Finish construction
        UUID emptyForestSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_FOREST);
        finishConstruction(accessTokenId, planetId, emptyForestSurfaceId);

        //Upgrade building
        buildingAtMaxLevel(accessTokenId, planetId);
        UUID upgradableBuildingId = findBuildingIdByDataId(accessTokenId, planetId, Constants.DATA_ID_HEADQUARTERS);
        upgradeBuilding(accessTokenId, planetId, upgradableBuildingId);
        constructionAlreadyInProgress(accessTokenId, planetId, upgradableBuildingId);
        cancel(accessTokenId, planetId, upgradableBuildingId);

        //Create and cancel deconstruction
        storageInUse(accessTokenId, planetId);
        buildingUnderConstruction(accessTokenId, emptyDesertSurfaceId, planetId);
        UUID buildingId = findBuildingIdByDataId(accessTokenId, planetId, Constants.DATA_ID_CAMP);
        deconstructBuilding(accessTokenId, planetId, buildingId);
        cancelDeconstruction(accessTokenId, planetId, buildingId);

        //Finish deconstruction
        finishDeconstruction(accessTokenId, planetId, buildingId);
    }

    //Create and cancel construction
    private static UUID invalidDataId(UUID accessTokenId, UUID planetId) {
        UUID emptyDesertSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        Response invalidDataIdResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(accessTokenId, planetId, emptyDesertSurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(invalidDataIdResponse, "dataId", "invalid value");
        return emptyDesertSurfaceId;
    }

    private void buildingAlreadyExists(UUID accessTokenId, UUID planetId) {
        UUID occupiedSurfaceId = findOccupiedMilitary(accessTokenId, planetId);

        Response buildingAlreadyExistsResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(accessTokenId, planetId, occupiedSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyErrorResponse(buildingAlreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private static void incompatibleSurfaceType(UUID accessTokenId, UUID planetId) {
        UUID emptyLakeSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);

        Response incompatibleSurfaceTypeResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(accessTokenId, planetId, emptyLakeSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyForbiddenOperation(incompatibleSurfaceTypeResponse);
    }

    private SurfaceResponse build(UUID accessTokenId, UUID planetId, UUID emptyDesertSurfaceId) {
        SkyXploreBuildingActions.constructNewBuilding(accessTokenId, planetId, emptyDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        SurfaceResponse modifiedSurface = findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), emptyDesertSurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding().getDataId()).isEqualTo(Constants.DATA_ID_SOLAR_PANEL);
        assertThat(modifiedSurface.getBuilding().getLevel()).isEqualTo(0);
        assertThat(modifiedSurface.getBuilding().getConstruction().getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = SkyXplorePlanetActions.getStorageOverview(accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(1);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(1);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(1);

        QueueResponse queueItem = planetOverviewResponse.getQueue()
            .stream()
            .filter(queueResponse -> Constants.DATA_ID_SOLAR_PANEL.equals(queueResponse.getData().get("dataId")))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Corresponding queueItem not found."));

        UUID constructionId = modifiedSurface.getBuilding().getConstruction().getConstructionId();
        assertThat(queueItem.getItemId()).isEqualTo(constructionId);
        assertThat(queueItem.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueItem.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
        assertThat(queueItem.getData()).containsEntry("currentLevel", 0);

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 1, 0);

        return modifiedSurface;
    }

    private void cancel(UUID accessTokenId, UUID planetId, UUID emptyDesertSurfaceId, SurfaceResponse modifiedSurface) {
        SkyXploreBuildingActions.cancelConstruction(accessTokenId, planetId, modifiedSurface.getBuilding().getBuildingId());

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        modifiedSurface = findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), emptyDesertSurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding()).isNull();

        PlanetStorageResponse storageResponse = SkyXplorePlanetActions.getStorageOverview(accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(0);

        assertThat(findSurfaceBySurfaceId(accessTokenId, planetId, emptyDesertSurfaceId).getBuilding()).isNull();
        assertThat(planetOverviewResponse.getQueue()).isEmpty();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 0, 0);
    }

    //Finish construction
    private void finishConstruction(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        SkyXploreBuildingActions.constructNewBuilding(accessTokenId, planetId, surfaceId, Constants.DATA_ID_CAMP);

        AwaitilityWrapper.createDefault()
            .until(() -> findSurfaceBySurfaceId(SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId), surfaceId)
                .flatMap(surfaceResponse -> Optional.ofNullable(surfaceResponse.getBuilding()))
                .isPresent()
            )
            .assertTrue("Construction is not started.");


        //Resume game
        SkyXploreGameActions.setPaused(accessTokenId, false);

        PlanetOverviewResponse planetOverviewResponse = AwaitilityWrapper.getWithWait(
                () -> SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId),
                por -> findSurfaceBySurfaceId(por.getSurfaces(), surfaceId)
                    .map(SurfaceResponse::getBuilding)
                    .filter(surfaceBuildingResponse -> isNull(surfaceBuildingResponse.getConstruction()))
                    .isPresent(),
                120,
                5
            )
            .orElseThrow(() -> new RuntimeException("Construction is not finished."));

        assertThat(planetOverviewResponse.getQueue()).isEmpty();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP, 1, 1);

        //Pause game again
        SkyXploreGameActions.setPaused(accessTokenId, true);
    }

    //Upgrade building
    private void buildingAtMaxLevel(UUID accessTokenId, UUID planetId) {
        UUID maxLevelBuildingId = findBuildingIdByDataId(accessTokenId, planetId, Constants.DATA_ID_CAMP);

        Response maxLevelResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(accessTokenId, planetId, maxLevelBuildingId);

        ResponseValidator.verifyForbiddenOperation(maxLevelResponse);
    }

    private static void upgradeBuilding(UUID accessTokenId, UUID planetId, UUID upgradableBuildingId) {
        SkyXploreBuildingActions.upgradeBuilding(accessTokenId, planetId, upgradableBuildingId);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        SurfaceResponse modifiedSurface = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), upgradableBuildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding().getConstruction().getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = planetOverviewResponse.getStorage();

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(500);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(200);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(500);

        QueueResponse queueItemModifiedEvent = planetOverviewResponse.getQueue()
            .stream()
            .filter(queueResponse -> queueResponse.getType().equals(Constants.QUEUE_TYPE_CONSTRUCTION))
            .findAny()
            .orElseThrow(() -> new RuntimeException("QueueItem not found."));

        UUID constructionId = modifiedSurface.getBuilding().getConstruction().getConstructionId();
        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(constructionId);
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("dataId", Constants.DATA_ID_HEADQUARTERS);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("currentLevel", 1);
    }

    private static void constructionAlreadyInProgress(UUID accessTokenId, UUID planetId, UUID upgradableBuildingId) {
        Response inProgressResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(accessTokenId, planetId, upgradableBuildingId);

        ResponseValidator.verifyErrorResponse(inProgressResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private void cancel(UUID accessTokenId, UUID planetId, UUID upgradableBuildingId) {
        SkyXploreBuildingActions.cancelConstruction(accessTokenId, planetId, upgradableBuildingId);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        SurfaceResponse modifiedSurface = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), upgradableBuildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding().getConstruction()).isNull();

        assertThat(findBuildingByBuildingId(accessTokenId, planetId, upgradableBuildingId).getConstruction()).isNull();

        assertThat(planetOverviewResponse.getQueue()).isEmpty();
    }

    //Create and cancel deconstruction
    private void storageInUse(UUID accessTokenId, UUID planetId) {
        UUID buildingId = findBuildingIdByDataId(accessTokenId, planetId, Constants.DATA_ID_HEADQUARTERS);
        Response response = SkyXploreBuildingActions.getDeconstructBuildingResponse(accessTokenId, planetId, buildingId);

        ResponseValidator.verifyErrorResponse(response, 400, ErrorCode.SKYXPLORE_STORAGE_USED);
    }

    private static void buildingUnderConstruction(UUID accessTokenId, UUID surfaceId, UUID planetId) {
        SkyXploreBuildingActions.constructNewBuilding(accessTokenId, planetId, surfaceId, Constants.DATA_ID_SOLAR_PANEL);
        UUID buildingId = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId)
            .getSurfaces()
            .stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Surface not found"))
            .getBuilding()
            .getBuildingId();

        Response constructionInProgressResponse = SkyXploreBuildingActions.getDeconstructBuildingResponse(accessTokenId, planetId, buildingId);

        ResponseValidator.verifyForbiddenOperation(constructionInProgressResponse);
    }

    private static void deconstructBuilding(UUID accessTokenId, UUID planetId, UUID buildingId) {
        SkyXploreBuildingActions.deconstructBuilding(accessTokenId, planetId, buildingId);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), buildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        DeconstructionResponse deconstruction = surfaceResponse.getBuilding()
            .getDeconstruction();
        assertThat(deconstruction).isNotNull();

        QueueResponse queueItem = planetOverviewResponse.getQueue()
            .stream()
            .filter(queueResponse -> Constants.DATA_ID_CAMP.equals(queueResponse.getData().get("dataId")))
            .findAny()
            .orElseThrow(() -> new RuntimeException("Corresponding queueItem not found."));

        assertThat(queueItem.getItemId()).isEqualTo(deconstruction.getDeconstructionId());
        assertThat(queueItem.getType()).isEqualTo(Constants.QUEUE_TYPE_DECONSTRUCTION);
        assertThat(queueItem.getData()).containsEntry("dataId", Constants.DATA_ID_CAMP);

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP, 1, 0);
    }

    private static void cancelDeconstruction(UUID accessTokenId, UUID planetId, UUID buildingId) {
        SkyXploreBuildingActions.cancelDeconstruction(accessTokenId, planetId, buildingId);

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverviewResponse.getSurfaces(), buildingId)
            .orElseThrow(() -> new RuntimeException("SurfaceResponse not found"));

        assertThat(surfaceResponse.getBuilding().getDeconstruction()).isNull();

        assertThat(planetOverviewResponse.getQueue()).extracting(QueueResponse::getType).doesNotContain(Constants.QUEUE_TYPE_DECONSTRUCTION);

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP, 1, 1);
    }

    //Finish deconstruction
    private void finishDeconstruction(UUID accessTokenId, UUID planetId, UUID buildingId) {
        SkyXploreBuildingActions.deconstructBuilding(accessTokenId, planetId, buildingId);

        SkyXploreGameActions.setPaused(accessTokenId, false);

        AwaitilityWrapper.create(180, 5)
            .until(() -> SkyXplorePlanetActions.findSurfaceByBuildingId(SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId), buildingId).isEmpty())
            .assertTrue("Building is not deconstructed.");

        PlanetBuildingDetailsValidator.verifyBuildingDetails(SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId).getBuildings(), Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP, 0, 0);

        SkyXploreGameActions.setPaused(accessTokenId, true);
    }

    //Helpers
    private Optional<SurfaceResponse> findSurfaceBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        assertThat(surfaces).isNotNull();

        return surfaces.stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findAny();
    }

    private SurfaceResponse findSurfaceBySurfaceId(UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Surface not found by surfaceId " + surfaceId));
    }

    private UUID findOccupiedMilitary(UUID accessTokenId, UUID planetId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(Constants.SURFACE_TYPE_MILITARY))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Occupied Military not found on planet " + planetId));
    }

    private SurfaceBuildingResponse findBuildingByBuildingId(UUID accessTokenId, UUID planetId, UUID buildingId) {
        return SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId)
            .stream()
            .map(SurfaceResponse::getBuilding)
            .filter(building -> !isNull(building))
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getBuildingId().equals(buildingId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building not found by buildingId " + buildingId));
    }

    private UUID findBuildingIdByDataId(UUID accessTokenId, UUID planetId, String dataId) {
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
