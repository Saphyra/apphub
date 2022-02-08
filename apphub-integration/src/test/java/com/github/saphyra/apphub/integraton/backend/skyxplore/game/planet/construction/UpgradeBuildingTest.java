package com.github.saphyra.apphub.integraton.backend.skyxplore.game.planet.construction;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetStorageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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

public class UpgradeBuildingTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void upgradeBuilding(Language language) {
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

        //Building at max level
        UUID maxLevelBuildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_BATTERY);

        Response maxLevelResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(language, accessTokenId, planetId, maxLevelBuildingId);

        ResponseValidator.verifyForbiddenOperation(language, maxLevelResponse);

        //Upgrade building
        UUID upgradableBuildingId = findBuilding(language, accessTokenId, planetId, Constants.DATA_ID_SOLAR_PANEL);

        SurfaceResponse modifiedSurface = SkyXploreBuildingActions.upgradeBuilding(language, accessTokenId, planetId, upgradableBuildingId);

        assertThat(modifiedSurface.getBuilding().getConstruction().getCurrentWorkPoints()).isEqualTo(0);

        PlanetStorageResponse storageResponse = SkyXplorePlanetStorageActions.getStorageOverview(language, accessTokenId, planetId);

        assertThat(storageResponse.getEnergy().getReservedStorageAmount()).isEqualTo(10);
        assertThat(storageResponse.getBulk().getReservedStorageAmount()).isEqualTo(10);
        assertThat(storageResponse.getLiquid().getReservedStorageAmount()).isEqualTo(10);

        QueueResponse queueItemModifiedEvent = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED + " event not arrived"))
            .getPayloadAs(QueueResponse.class);

        UUID constructionId = modifiedSurface.getBuilding().getConstruction().getConstructionId();
        assertThat(queueItemModifiedEvent.getItemId()).isEqualTo(constructionId);
        assertThat(queueItemModifiedEvent.getType()).isEqualTo(Constants.QUEUE_TYPE_CONSTRUCTION);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
        assertThat(queueItemModifiedEvent.getData()).containsEntry("currentLevel", 1);

        //Construction already in progress
        Response inProgressResponse = SkyXploreBuildingActions.getUpgradeBuildingResponse(language, accessTokenId, planetId, upgradableBuildingId);

        ResponseValidator.verifyErrorResponse(language, inProgressResponse, 409, ErrorCode.ALREADY_EXISTS);

        //Cancel
        modifiedSurface = SkyXploreBuildingActions.cancelConstruction(language, accessTokenId, planetId, upgradableBuildingId);

        assertThat(modifiedSurface.getBuilding().getConstruction()).isNull();

        assertThat(findByBuildingId(language, accessTokenId, planetId, upgradableBuildingId).getConstruction()).isNull();

        UUID payload = gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED)
            .orElseThrow(() -> new RuntimeException(WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED + " event not arrived"))
            .getPayloadAs(UUID.class);

        assertThat(payload).isEqualTo(constructionId);
    }

    private SurfaceBuildingResponse findByBuildingId(Language language, UUID accessTokenId, UUID planetId, UUID buildingId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .map(SurfaceResponse::getBuilding)
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getBuildingId().equals(buildingId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building not found by buildingId " + buildingId));
    }

    private UUID findBuilding(Language language, UUID accessTokenId, UUID planetId, String dataId) {
        return SkyXplorePlanetActions.getSurfaces(language, accessTokenId, planetId)
            .stream()
            .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
            .map(SurfaceResponse::getBuilding)
            .filter(surfaceBuildingResponse -> surfaceBuildingResponse.getDataId().equals(dataId))
            .map(SurfaceBuildingResponse::getBuildingId)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Building " + dataId + " not found on planet " + planetId));
    }
}
