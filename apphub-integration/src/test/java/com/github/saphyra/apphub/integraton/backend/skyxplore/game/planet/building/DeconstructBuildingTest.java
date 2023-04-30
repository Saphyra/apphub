package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet.building;

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
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.skyxplore.DeconstructionResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.PlanetStorageResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.skyxplore.SurfaceBuildingResponse;
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

public class DeconstructBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void deconstructBuilding(Language language) {
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

        //Storage in use
        UUID buildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_DEPOT);
        Response response = SkyXploreBuildingActions.getDeconstructBuildingResponse(language, accessTokenId, planetId, buildingId);

        ResponseValidator.verifyErrorResponse(response, 400, ErrorCode.SKYXPLORE_STORAGE_USED);

        //Building under construction
        UUID emptyDesertSurfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(language, accessTokenId, planetId, Constants.SURFACE_TYPE_LAKE);
        SkyXploreBuildingActions.constructNewBuilding(language, accessTokenId, planetId, emptyDesertSurfaceId, Constants.DATA_ID_WATER_PUMP);
        buildingId = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> !isNull(webSocketEvent.getPayloadAs(SurfaceResponse.class).getBuilding()))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class)
            .getBuilding()
            .getBuildingId();

        Response constructionInProgressResponse = SkyXploreBuildingActions.getDeconstructBuildingResponse(language, accessTokenId, planetId, buildingId);

        ResponseValidator.verifyForbiddenOperation(language, constructionInProgressResponse);

        //Deconstruct building
        gameWsClient.clearMessages();

        buildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_BATTERY);

        SkyXploreBuildingActions.deconstructBuilding(language, accessTokenId, planetId, buildingId);
        SurfaceResponse surfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> !isNull(webSocketEvent.getPayloadAs(SurfaceResponse.class).getBuilding()))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        DeconstructionResponse deconstruction = surfaceResponse.getBuilding()
            .getDeconstruction();
        assertThat(deconstruction).isNotNull();

        QueueResponse queueItemModifiedEvent = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED + " event not arrived"))
            .getPayloadAs(QueueResponse.class);

        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(deconstruction.getDeconstructionId());
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_DECONSTRUCTION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("dataId", Constants.DATA_ID_BATTERY);

        Object buildingDetails = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED + " event not arrived"))
            .getPayload();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(buildingDetails, Constants.SURFACE_TYPE_CONCRETE, Constants.DATA_ID_BATTERY, 1, 0);

        PlanetStorageResponse planetStorageResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED)
            .orElseThrow()
            .getPayloadAs(PlanetStorageResponse.class);

        assertThat(planetStorageResponse.getEnergy().getCapacity()).isEqualTo(0);

        //Cancel deconstruction
        gameWsClient.clearMessages();

        SkyXploreBuildingActions.cancelDeconstruction(language, accessTokenId, planetId, buildingId);
        surfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> !isNull(webSocketEvent.getPayloadAs(SurfaceResponse.class).getBuilding()))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        assertThat(surfaceResponse.getBuilding().getDeconstruction()).isNull();

        UUID payload = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED + " event not arrived"))
            .getPayloadAs(UUID.class);

        assertThat(payload).isEqualTo(deconstruction.getDeconstructionId());

        buildingDetails = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED + " event not arrived"))
            .getPayload();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(buildingDetails, Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 1, 1);
    }

    @Test(groups = "skyxplore")
    public void finishDeconstruction() {
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

        WsActions.sendSkyXplorePageOpenedMessage(gameWsClient, Constants.PAGE_TYPE_PLANET, planetId);

        UUID buildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_SOLAR_PANEL);
        SkyXploreBuildingActions.deconstructBuilding(language, accessTokenId, planetId, buildingId);
        SurfaceResponse surfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> !isNull(webSocketEvent.getPayloadAs(SurfaceResponse.class).getBuilding()))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(language, accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED, 120, webSocketEvent -> UUID.fromString(webSocketEvent.getPayload().toString()).equals(surfaceResponse.getBuilding().getDeconstruction().getDeconstructionId()))
            .orElseThrow(() -> new RuntimeException("Construction is not finished."));

        Object buildingDetails = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED + " event not arrived"))
            .getPayload();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(buildingDetails, Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 0, 0);
    }

    private UUID findBuilding(Language language, UUID accessTokenId, UUID planetId, String dataId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .map(SurfaceResponse::getBuilding)
            .filter(building -> !isNull(building))
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getDataId().equals(dataId))
            .map(SurfaceBuildingResponse::getBuildingId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException(dataId + " building not found on planet " + planetId));
    }
}
