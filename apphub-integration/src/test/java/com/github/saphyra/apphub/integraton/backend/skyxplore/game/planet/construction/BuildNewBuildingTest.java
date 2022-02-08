package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetStorageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.WsActions;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class BuildNewBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
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
        WsActions.sendSkyXplorePageOpenedMessage(gameWsClient, Constants.PAGE_TYPE_PLANET, planetId);

        //Invalid dataId
        UUID emptyDesertSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(language, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        Response invalidDataIdResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, emptyDesertSurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(language, invalidDataIdResponse, "dataId", "invalid value");

        //Building already exists
        UUID occupiedDesertSurfaceId = findOccupiedDesert(language, accessTokenId, planetId);

        Response buildingAlreadyExistsResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, occupiedDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyErrorResponse(language, buildingAlreadyExistsResponse, 409, ErrorCode.ALREADY_EXISTS);

        //Incompatible surfaceType
        UUID emptyLakeSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(language, accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);

        Response incompatibleSurfaceTypeResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, emptyLakeSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyForbiddenOperation(language, incompatibleSurfaceTypeResponse);

        //Build
        SurfaceResponse modifiedSurface = SkyXploreBuildingActions.constructNewBuilding(language, accessTokenId, planetId, emptyDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        assertThat(modifiedSurface.getBuilding().getDataId()).isEqualTo(Constants.DATA_ID_SOLAR_PANEL);
        assertThat(modifiedSurface.getBuilding().getLevel()).isEqualTo(0);
        assertThat(modifiedSurface.getBuilding().getConstruction().getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = SkyXplorePlanetStorageActions.getStorageOverview(language, accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(1);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(1);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(1);

        QueueResponse queueItemModifiedEvent = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED + " event not arrived"))
            .getPayloadAs(QueueResponse.class);

        UUID constructionId = modifiedSurface.getBuilding().getConstruction().getConstructionId();
        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(constructionId);
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("currentLevel", 0);

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED + " event not arrived"));

        //Cancel
        gameWsClient.clearMessages();
        modifiedSurface = SkyXploreBuildingActions.cancelConstruction(language, accessTokenId, planetId, modifiedSurface.getBuilding().getBuildingId());

        assertThat(modifiedSurface.getBuilding()).isNull();

        storageResponse = SkyXplorePlanetStorageActions.getStorageOverview(language, accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(0);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(0);

        assertThat(findBySurfaceId(language, accessTokenId, planetId, emptyDesertSurfaceId).getBuilding()).isNull();

        UUID payload = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED + " event not arrived"))
            .getPayloadAs(UUID.class);

        assertThat(payload).isEqualTo(constructionId);

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED + " event not arrived"));

        //Terraformation in progress
        SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptyDesertSurfaceId, Constants.SURFACE_TYPE_LAKE);

        Response terraformationInProgressResponse = SkyXploreBuildingActions.getConstructNewBuildingResponse(language, accessTokenId, planetId, emptyDesertSurfaceId, Constants.DATA_ID_SOLAR_PANEL);

        ResponseValidator.verifyErrorResponse(language, terraformationInProgressResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private SurfaceResponse findBySurfaceId(Language language, UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Surface not found by surfaceId " + surfaceId));
    }


    private UUID findOccupiedDesert(Language language, UUID accessTokenId, UUID planetId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(Constants.SURFACE_TYPE_DESERT))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Occupied Desert not found on planet " + planetId));
    }
}
