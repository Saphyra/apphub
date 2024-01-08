package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.PlanetBuildingDetailsValidator;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceBuildingResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class DeconstructionQueueTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "skyxplore"})
    public void deconstructionQueueCrud(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId)
            .getPlanetId();

        UUID buildingId = findBuilding(accessTokenId, planetId, Constants.DATA_ID_SOLAR_PANEL);

        SkyXploreBuildingActions.deconstructBuilding(language, accessTokenId, planetId, buildingId);
        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceByBuildingId(SkyXplorePlanetActions.getSurfaces(accessTokenId, planetId), buildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        QueueResponse queueResponse = getQueue(accessTokenId, planetId, surfaceResponse);
        updatePriority_invalidType(language, accessTokenId, planetId, queueResponse);
        updatePriority_priorityTooLow(language, accessTokenId, planetId, queueResponse);
        updatePriority_priorityTooHigh(language, accessTokenId, planetId, queueResponse);
        queueResponse = updatePriority(language, accessTokenId, planetId, queueResponse);
        cancelDeconstruction(language, accessTokenId, planetId, queueResponse, buildingId);
    }

    private static QueueResponse getQueue(UUID accessTokenId, UUID planetId, SurfaceResponse surfaceResponse) {
        List<QueueResponse> queue = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId)
            .getQueue();

        assertThat(queue).hasSize(1);
        QueueResponse queueResponse = queue.get(0);
        assertThat(queueResponse.getItemId()).isEqualTo(surfaceResponse.getBuilding().getDeconstruction().getDeconstructionId());
        assertThat(queueResponse.getType()).isEqualTo(Constants.QUEUE_TYPE_DECONSTRUCTION);
        assertThat(queueResponse.getRequiredWorkPoints()).isEqualTo(surfaceResponse.getBuilding().getDeconstruction().getRequiredWorkPoints());
        assertThat(queueResponse.getOwnPriority()).isEqualTo(Constants.DEFAULT_PRIORITY);
        assertThat(queueResponse.getData()).containsEntry("dataId", Constants.DATA_ID_SOLAR_PANEL);
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

    private QueueResponse updatePriority(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        SkyXplorePlanetQueueActions.setPriority(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 7);

        queueResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId)
            .getQueue()
            .get(0);

        assertThat(queueResponse.getOwnPriority()).isEqualTo(7);


        return queueResponse;
    }

    private static void cancelDeconstruction(Language language, UUID accessTokenId, UUID planetId, QueueResponse queueResponse, UUID buildingId) {
        SkyXplorePlanetQueueActions.cancelItem(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        PlanetOverviewResponse planetOverview = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);
        assertThat(planetOverview.getQueue()).isEmpty();

        SurfaceResponse surfaceResponse = SkyXplorePlanetActions.findSurfaceByBuildingId(planetOverview.getSurfaces(), buildingId)
            .orElseThrow(() -> new RuntimeException("Surface not found."));

        assertThat(surfaceResponse.getBuilding().getDeconstruction()).isNull();

        PlanetBuildingDetailsValidator.verifyBuildingDetails(planetOverview.getBuildings(), Constants.SURFACE_TYPE_DESERT, Constants.DATA_ID_SOLAR_PANEL, 1, 1);
    }

    private UUID findBuilding(UUID accessTokenId, UUID planetId, String dataId) {
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
