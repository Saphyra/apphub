package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.skyxplore.PlanetBuildingDetailsValidator;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstructionQueueTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void constructionQueueCrud(Language language) {
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

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(language, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreBuildingActions.constructNewBuilding(language, accessTokenId, planetId, surfaceId, Constants.DATA_ID_SOLAR_PANEL);
        SurfaceResponse newBuilding = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> !isNull(webSocketEvent.getPayloadAs(SurfaceResponse.class).getBuilding()))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        //Get queue
        List<QueueResponse> queue = SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId);

        assertThat(queue).hasSize(1);
        QueueResponse queueResponse = queue.get(0);
        assertThat(queueResponse.getItemId()).isEqualTo(newBuilding.getBuilding().getConstruction().getConstructionId());
        assertThat(queueResponse.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueResponse.getRequiredWorkPoints()).isEqualTo(newBuilding.getBuilding().getConstruction().getRequiredWorkPoints());
        assertThat(queueResponse.getOwnPriority()).isEqualTo(Constants.DEFAULT_PRIORITY);
        assertThat(queueResponse.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
        assertThat(queueResponse.getData()).containsEntry("currentLevel", 0);

        //Update priority - Invalid type
        Response setPriority_invalidTypeResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(language, accessTokenId, planetId, "asd", queueResponse.getItemId(), 4);

        ResponseValidator.verifyInvalidParam(language, setPriority_invalidTypeResponse, "type", "invalid value");

        //Update priority - Priority too low
        Response setPriority_priorityTooLowResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 0);

        ResponseValidator.verifyInvalidParam(language, setPriority_priorityTooLowResponse, "priority", "too low");

        //Update priority - Priority too high
        Response setPriority_priorityTooHighResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 11);

        ResponseValidator.verifyInvalidParam(language, setPriority_priorityTooHighResponse, "priority", "too high");

        //Update priority
        gameWsClient.clearMessages();
        SkyXplorePlanetQueueActions.setPriority(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 7);

        queueResponse = SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId)
            .get(0);

        assertThat(queueResponse.getOwnPriority()).isEqualTo(7);

        QueueResponse queueItemModifiedEvent = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED + " event not arrived"))
            .getPayloadAs(QueueResponse.class);

        assertThat(queueItemModifiedEvent.getOwnPriority()).isEqualTo(7);

        //Cancel construction - Invalid type
        Response cancelConstruction_invalidTypeResponse = SkyXplorePlanetQueueActions.getCancelItemResponse(language, accessTokenId, planetId, "asd", queueResponse.getItemId());

        ResponseValidator.verifyInvalidParam(language, cancelConstruction_invalidTypeResponse, "type", "invalid value");

        //Cancel construction
        gameWsClient.clearMessages();

        SkyXplorePlanetQueueActions.cancelItem(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        assertThat(SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId)).isEmpty();

        UUID payload = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED + " event not arrived"))
            .getPayloadAs(UUID.class);

        assertThat(payload).isEqualTo(queueItemModifiedEvent.getItemId());

        SurfaceResponse surfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED + " event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        assertThat(surfaceResponse.getBuilding()).isNull();

        Object buildingDetails = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED + " event not arrived"))
            .getPayload();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(buildingDetails, Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 1, 1);
    }
}
