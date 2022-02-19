package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet.terraform;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
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

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class TerraformTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
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

        //Invalid surfaceType
        Response invalidSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptySurfaceId, "asd");

        ResponseValidator.verifyInvalidParam(language, invalidSurfaceTypeResponse, "surfaceType", "invalid value");

        //Surface not empty
        UUID occupiedSurfaceId = findOccupiedDesert(language, accessTokenId, planetId);

        Response surfaceOccupiedResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, occupiedSurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyForbiddenOperation(language, surfaceOccupiedResponse);

        //Incompatible surfaceType
        Response incompatibleSurfaceTypeResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_OIL_FIELD);

        ResponseValidator.verifyForbiddenOperation(language, incompatibleSurfaceTypeResponse);

        //Terraform
        SurfaceResponse modifiedSurfaceResponse = SkyXploreSurfaceActions.terraform(language, accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

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

        //Terraformation already in progress
        Response alreadyInProgressResponse = SkyXploreSurfaceActions.getTerraformResponse(language, accessTokenId, planetId, emptySurfaceId, Constants.SURFACE_TYPE_LAKE);

        ResponseValidator.verifyErrorResponse(language, alreadyInProgressResponse, 409, ErrorCode.ALREADY_EXISTS);

        //Cancel
        gameWsClient.clearMessages();
        modifiedSurfaceResponse = SkyXploreSurfaceActions.cancelTerraformation(language, accessTokenId, planetId, emptySurfaceId);

        assertThat(modifiedSurfaceResponse.getTerraformation()).isNull();

        UUID payload = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED + " event not arrived"))
            .getPayloadAs(UUID.class);
        assertThat(payload).isEqualTo(constructionId);

        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED + " event not arrived"));

        assertThat(findBySurfaceId(language, accessTokenId, planetId, emptySurfaceId).getTerraformation()).isNull();
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
