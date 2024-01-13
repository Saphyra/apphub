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
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BuildNewBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void constructNewBuilding() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();

        UUID emptyDesertSurfaceId = invalidDataId(accessTokenId, planetId);
        buildingAlreadyExists(accessTokenId, planetId);
        incompatibleSurfaceType(accessTokenId, planetId);
        SurfaceResponse modifiedSurface = build(accessTokenId, planetId, emptyDesertSurfaceId);
        cancel(accessTokenId, planetId, emptyDesertSurfaceId, modifiedSurface);
    }

    private static UUID invalidDataId(UUID accessTokenId, UUID planetId) {
        UUID emptyDesertSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        Response invalidDataIdResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(accessTokenId, planetId, emptyDesertSurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(invalidDataIdResponse, "dataId", "invalid value");
        return emptyDesertSurfaceId;
    }

    private void buildingAlreadyExists(UUID accessTokenId, UUID planetId) {
        UUID occupiedDesertSurfaceId = findOccupiedDesert(accessTokenId, planetId);

        Response buildingAlreadyExistsResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(accessTokenId, planetId, occupiedDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

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

        SurfaceResponse modifiedSurface = findBySurfaceId(planetOverviewResponse.getSurfaces(), emptyDesertSurfaceId)
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

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 2, 1);

        return modifiedSurface;
    }

    private Optional<SurfaceResponse> findBySurfaceId(List<SurfaceResponse> surfaces, UUID surfaceId) {
        assertThat(surfaces).isNotNull();

        return surfaces.stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findAny();
    }

    private void cancel(UUID accessTokenId, UUID planetId, UUID emptyDesertSurfaceId, SurfaceResponse modifiedSurface) {
        SkyXploreBuildingActions.cancelConstruction(accessTokenId, planetId, modifiedSurface.getBuilding().getBuildingId());

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        modifiedSurface = findBySurfaceId(planetOverviewResponse.getSurfaces(), emptyDesertSurfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(modifiedSurface.getBuilding()).isNull();

        PlanetStorageResponse storageResponse = SkyXplorePlanetActions.getStorageOverview(accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(0);

        assertThat(findBySurfaceId(accessTokenId, planetId, emptyDesertSurfaceId).getBuilding()).isNull();
        assertThat(planetOverviewResponse.getQueue()).isEmpty();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 1, 1);
    }

    @Test(groups = {"be", "skyxplore"})
    void finishConstruction() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_FOREST);

        //Start construction
        SkyXploreBuildingActions.constructNewBuilding(accessTokenId, planetId, surfaceId, Constants.DATA_ID_CAMP);

        AwaitilityWrapper.createDefault()
            .until(() -> findBySurfaceId(SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId), surfaceId)
                .flatMap(surfaceResponse -> Optional.ofNullable(surfaceResponse.getBuilding()))
                .isPresent()
            )
            .assertTrue("Construction is not started.");


        //Resume game
        SkyXploreGameActions.setPaused(accessTokenId, false);

        PlanetOverviewResponse planetOverviewResponse = AwaitilityWrapper.getWithWait(
                () -> SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId),
                por -> findBySurfaceId(por.getSurfaces(), surfaceId)
                    .map(SurfaceResponse::getBuilding)
                    .filter(surfaceBuildingResponse -> isNull(surfaceBuildingResponse.getConstruction()))
                    .isPresent(),
                120,
                5
            )
            .orElseThrow(() -> new RuntimeException("Construction is not finished."));

        assertThat(planetOverviewResponse.getQueue()).isEmpty();


        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverviewResponse.getBuildings(), Constants.SURFACE_TYPE_FOREST, Constants.DATA_ID_CAMP, 2, 2);
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
