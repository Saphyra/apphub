package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TerraformationQueueTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void terraformationQueueCrud() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(accessTokenId)
            .getPlanetId();

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_LAKE);

        QueueResponse queueResponse = getQueueResponse(accessTokenId, planetId);
        updatePriority_invalidType(accessTokenId, planetId, queueResponse);
        updatePriority_priorityTooLow(accessTokenId, planetId, queueResponse);
        updatePriority_priorityTooHigh(accessTokenId, planetId, queueResponse);
        queueResponse = updatePriority(accessTokenId, planetId, queueResponse);
        cancelConstruction_invalidType(accessTokenId, planetId, queueResponse);
        cancelConstruction(accessTokenId, planetId, queueResponse, surfaceId);
    }

    private static QueueResponse getQueueResponse(UUID accessTokenId, UUID planetId) {
        List<QueueResponse> queue = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId)
            .getQueue();

        assertThat(queue).hasSize(1);
        QueueResponse queueResponse = queue.get(0);
        assertThat(queueResponse.getType()).isEqualTo(Constants.QUEUE_TYPE_TERRAFORMATION);
        assertThat(queueResponse.getOwnPriority()).isEqualTo(Constants.DEFAULT_PRIORITY);
        assertThat(queueResponse.getData()).containsEntry("currentSurfaceType", Constants.SURFACE_TYPE_DESERT);
        assertThat(queueResponse.getData()).containsEntry("targetSurfaceType", Constants.SURFACE_TYPE_LAKE);
        return queueResponse;
    }

    private static void updatePriority_invalidType(UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_invalidTypeResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(accessTokenId, planetId, "asd", queueResponse.getItemId(), 4);

        ResponseValidator.verifyInvalidParam(setPriority_invalidTypeResponse, "type", "invalid value");
    }

    private static void updatePriority_priorityTooLow(UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_priorityTooLowResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 0);

        ResponseValidator.verifyInvalidParam(setPriority_priorityTooLowResponse, "priority", "too low");
    }

    private static void updatePriority_priorityTooHigh(UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_priorityTooHighResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 11);

        ResponseValidator.verifyInvalidParam(setPriority_priorityTooHighResponse, "priority", "too high");
    }

    private QueueResponse updatePriority(UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        SkyXplorePlanetQueueActions.setPriority(accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 7);

        queueResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId)
            .getQueue()
            .get(0);

        assertThat(queueResponse.getOwnPriority()).isEqualTo(7);

        return queueResponse;
    }

    private static void cancelConstruction_invalidType(UUID accessTokenId, UUID planetId, QueueResponse queueResponse) {
        Response cancelConstruction_invalidTypeResponse = SkyXplorePlanetQueueActions.getCancelItemResponse(accessTokenId, planetId, "asd", queueResponse.getItemId());

        ResponseValidator.verifyInvalidParam(cancelConstruction_invalidTypeResponse, "type", "invalid value");
    }

    private static void cancelConstruction(UUID accessTokenId, UUID planetId, QueueResponse queueResponse, UUID surfaceId) {
        SkyXplorePlanetQueueActions.cancelItem(accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(accessTokenId, planetId);

        assertThat(planetOverviewResponse.getQueue()).isEmpty();
        assertThat(SkyXplorePlanetActions.findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), surfaceId).orElseThrow(() -> new RuntimeException("Surface not found")).getTerraformation()).isNull();
    }
}
