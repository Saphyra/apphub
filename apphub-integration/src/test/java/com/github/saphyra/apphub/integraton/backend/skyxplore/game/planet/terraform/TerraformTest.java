package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet.terraform;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
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

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TerraformTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void terraformCD(Language language) {
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

        UUID emptySurfaceId = findEmptySurface(language, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        invalidSurfaceType(language, accessTokenId, planetId, emptySurfaceId);
        surfaceNotEmpty(language, accessTokenId, planetId);
        incompatibleSurfaceType(language, accessTokenId, planetId, emptySurfaceId);
        UUID constructionId = terraform(language, accessTokenId, gameWsClient, planetId, emptySurfaceId);
        terraformationAlreadyInProgress(language, accessTokenId, planetId, emptySurfaceId);
        cancel(language, accessTokenId, gameWsClient, planetId, emptySurfaceId, constructionId);
    }

    private static void invalidSurfaceType(Language language, UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        Response invalidSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptySurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(language, invalidSurfaceTypeResponse, "surfaceType", "invalid value");
    }

    private void surfaceNotEmpty(Language language, UUID accessTokenId, UUID planetId) {
        UUID occupiedSurfaceId = findOccupiedDesert(language, accessTokenId, planetId);

        Response surfaceOccupiedResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, occupiedSurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyForbiddenOperation(language, surfaceOccupiedResponse);
    }

    private static void incompatibleSurfaceType(Language language, UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        Response incompatibleSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_OIL_FIELD);

        ResponseValidator.verifyForbiddenOperation(language, incompatibleSurfaceTypeResponse);
    }

    private UUID terraform(Language language, UUID accessTokenId, ApphubWsClient gameWsClient, UUID planetId, UUID emptySurfaceId) {
        SkyXploreSurfaceActions.terraform(language, accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);
        SurfaceResponse modifiedSurfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> webSocketEvent.getPayloadAs(SurfaceResponse.class).getSurfaceId().equals(emptySurfaceId))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        assertThat(modifiedSurfaceResponse.getTerraformation()).isNotNull();

        QueueResponse queueItemModifiedEvent = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED + " event not arrived"))
            .getPayloadAs(QueueResponse.class);

        UUID constructionId = modifiedSurfaceResponse.getTerraformation().getConstructionId();
        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(constructionId);
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_TERRAFORMATION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("currentSurfaceType", Constants.SURFACE_TYPE_DESERT);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("targetSurfaceType", Constants.SURFACE_TYPE_LAKE);

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED + " event not arrived"));

        assertThat(findBySurfaceId(language, accessTokenId, planetId, emptySurfaceId).getTerraformation()).isNotNull();
        return constructionId;
    }

    private static void terraformationAlreadyInProgress(Language language, UUID accessTokenId, UUID planetId, UUID emptySurfaceId) {
        Response alreadyInProgressResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyErrorResponse(language, alreadyInProgressResponse, 409, ErrorCode.ALREADY_EXISTS);
    }

    private void cancel(Language language, UUID accessTokenId, ApphubWsClient gameWsClient, UUID planetId, UUID emptySurfaceId, UUID constructionId) {
        SurfaceResponse modifiedSurfaceResponse;
        gameWsClient.clearMessages();
        SkyXploreSurfaceActions.cancelTerraformation(language, accessTokenId, planetId, emptySurfaceId);
        modifiedSurfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> webSocketEvent.getPayloadAs(SurfaceResponse.class).getSurfaceId().equals(emptySurfaceId))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        assertThat(modifiedSurfaceResponse.getTerraformation()).isNull();

        UUID payload = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED + " event not arrived"))
            .getPayloadAs(UUID.class);
        assertThat(payload).isEqualTo(constructionId);

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED + " event not arrived"));

        assertThat(findBySurfaceId(language, accessTokenId, planetId, emptySurfaceId).getTerraformation()).isNull();
    }

    @Test(groups = {"be", "skyxplore"})
    public void finishTerraformation() {
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

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(language, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(language, accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_CONCRETE);
        SurfaceResponse modifiedSurface = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, webSocketEvent -> webSocketEvent.getPayloadAs(SurfaceResponse.class).getSurfaceId().equals(surfaceId))
            .orElseThrow(() -> new RuntimeException("SurfaceModified event not arrived"))
            .getPayloadAs(SurfaceResponse.class);

        assertThat(modifiedSurface.getTerraformation()).isNotNull();

        gameWsClient.clearMessages();

        SkyXploreGameActions.setPaused(language, accessTokenId, false);

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED, 120, webSocketEvent -> UUID.fromString(webSocketEvent.getPayload().toString()).equals(modifiedSurface.getTerraformation().getConstructionId()))
            .orElseThrow(() -> new RuntimeException("Terraformation is not finished."));

        SurfaceResponse surfaceResponse = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, 120, webSocketEvent -> isNull(webSocketEvent.getPayloadAs(SurfaceResponse.class).getTerraformation()))
            .orElseThrow(() -> new RuntimeException("Terraformation is not finished."))
            .getPayloadAs(SurfaceResponse.class);

        assertThat(surfaceResponse.getTerraformation()).isNull();
        assertThat(surfaceResponse.getSurfaceType()).isEqualTo(Constants.SURFACE_TYPE_CONCRETE);
    }

    private SurfaceResponse findBySurfaceId(Language language, UUID accessTokenId, UUID planetId, UUID surfaceId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> surfaceResponse.getSurfaceId().equals(surfaceId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Surface not found by surfaceId " + surfaceId));
    }

    private UUID findEmptySurface(Language language, UUID accessTokenId, UUID planetId, String surfaceType) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> isNull(surfaceResponse.getBuilding()))
            .filter(surfaceResponse -> surfaceResponse.getSurfaceType().equals(surfaceType))
            .findFirst()
            .map(SurfaceResponse::getSurfaceId)
            .orElseThrow(() -> new RuntimeException("Empty Desert not found on planet " + planetId));
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
