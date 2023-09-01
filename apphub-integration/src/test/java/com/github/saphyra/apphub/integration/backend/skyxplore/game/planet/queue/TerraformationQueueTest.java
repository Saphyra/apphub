package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TerraformationQueueTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void terraformationQueueCrud(Language language) {
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

        SkyXploreSurfaceActions.terraform(language, accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_LAKE);

        QueueResponse queueResponse = getQueueResponse(language, accessTokenId, planetId);
        updatePriority_invalidType(language, accessTokenId, planetId, queueResponse);
        updatePriority_priorityTooLow(language, accessTokenId, planetId, queueResponse);
        updatePriority_priorityTooHigh(language, accessTokenId, planetId, queueResponse);
        BiWrapper<QueueResponse, QueueResponse> updateResult = updatePriority(language, accessTokenId, planetId, queueResponse, gameWsClient);
        queueResponse = updateResult.getEntity1();
        QueueResponse queueItemModifiedEvent = updateResult.getEntity2();
        cancelConstruction_invalidType(language, accessTokenId, planetId, queueResponse);
        cancelConstruction(language, accessTokenId, gameWsClient, planetId, queueResponse, queueItemModifiedEvent);
    }

    private static QueueResponse getQueueResponse(Language language, UUID accessTokenId, UUID planetId) {
        List<QueueResponse> queue = SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId);

        assertThat(queue).hasSize(1);
        QueueResponse queueResponse = queue.get(0);
        assertThat(queueResponse.getType()).isEqualTo(Constants.QUEUE_TYPE_TERRAFORMATION);
        assertThat(queueResponse.getOwnPriority()).isEqualTo(Constants.DEFAULT_PRIORITY);
        assertThat(queueResponse.getData()).containsEntry("currentSurfaceType", Constants.SURFACE_TYPE_DESERT);
        assertThat(queueResponse.getData()).containsEntry("targetSurfaceType", Constants.SURFACE_TYPE_LAKE);
        return queueResponse;
    }

    private static void updatePriority_invalidType(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_invalidTypeResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(language, accessTokenId, planetId, "asd", queueResponse.getItemId(), 4);

        ResponseValidator.verifyInvalidParam(language, setPriority_invalidTypeResponse, "type", "invalid value");
    }

    private static void updatePriority_priorityTooLow(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_priorityTooLowResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 0);

        ResponseValidator.verifyInvalidParam(language, setPriority_priorityTooLowResponse, "priority", "too low");
    }

    private static void updatePriority_priorityTooHigh(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_priorityTooHighResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 11);

        ResponseValidator.verifyInvalidParam(language, setPriority_priorityTooHighResponse, "priority", "too high");
    }

    private BiWrapper<QueueResponse, QueueResponse> updatePriority(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse, ApphubWsClient gameWsClient) {
        gameWsClient.clearMessages();
        SkyXplorePlanetQueueActions.setPriority(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 7);

        queueResponse = SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId)
            .get(0);

        assertThat(queueResponse.getOwnPriority()).isEqualTo(7);

        QueueResponse queueItemModifiedEvent = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED + " event not arrived"))
            .getPayloadAs(QueueResponse.class);

        assertThat(queueItemModifiedEvent.getOwnPriority()).isEqualTo(7);

        return new BiWrapper<>(queueResponse, queueItemModifiedEvent);
    }

    private static void cancelConstruction_invalidType(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response cancelConstruction_invalidTypeResponse = SkyXplorePlanetQueueActions.getCancelItemResponse(language, accessTokenId, planetId, "asd", queueResponse.getItemId());

        ResponseValidator.verifyInvalidParam(language, cancelConstruction_invalidTypeResponse, "type", "invalid value");
    }

    private static void cancelConstruction(Language language, UUID accessTokenId, ApphubWsClient gameWsClient, UUID planetId, QueueResponse queueResponse, QueueResponse queueItemModifiedEvent) {
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
    }
}
