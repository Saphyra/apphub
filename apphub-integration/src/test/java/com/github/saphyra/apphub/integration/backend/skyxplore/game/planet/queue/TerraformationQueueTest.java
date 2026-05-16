package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.PlanetOverviewResponse;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessToken, characterModel1);
        UUID userId1 = DynamoDbUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessToken, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessToken)
            .getPlanetId();

        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(getServerPort(), accessToken, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(getServerPort(), accessToken, planetId, surfaceId, Constants.SURFACE_TYPE_LAKE);

        QueueResponse queueResponse = getQueueResponse(accessToken, planetId);
        updatePriority_invalidType(accessToken, planetId, queueResponse);
        updatePriority_priorityTooLow(accessToken, planetId, queueResponse);
        updatePriority_priorityTooHigh(accessToken, planetId, queueResponse);
        queueResponse = updatePriority(accessToken, planetId, queueResponse);
        cancelConstruction_invalidType(accessToken, planetId, queueResponse);
        cancelConstruction(accessToken, planetId, queueResponse, surfaceId);
    }

    private static QueueResponse getQueueResponse(String accessToken, UUID planetId) {
        List<QueueResponse> queue = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessToken, planetId)
            .getQueue();

        assertThat(queue).hasSize(1);
        QueueResponse queueResponse = queue.get(0);
        assertThat(queueResponse.getType()).isEqualTo(Constants.QUEUE_TYPE_TERRAFORMATION);
        assertThat(queueResponse.getOwnPriority()).isEqualTo(Constants.DEFAULT_PRIORITY);
        assertThat(queueResponse.getData()).containsEntry("currentSurfaceType", Constants.SURFACE_TYPE_DESERT);
        assertThat(queueResponse.getData()).containsEntry("targetSurfaceType", Constants.SURFACE_TYPE_LAKE);
        return queueResponse;
    }

    private static void updatePriority_invalidType(String accessToken, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_invalidTypeResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(getServerPort(), accessToken, planetId, "asd", queueResponse.getItemId(), 4);

        ResponseValidator.verifyInvalidParam(setPriority_invalidTypeResponse, "type", "invalid value");
    }

    private static void updatePriority_priorityTooLow(String accessToken, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_priorityTooLowResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(getServerPort(), accessToken, planetId, queueResponse.getType(), queueResponse.getItemId(), 0);

        ResponseValidator.verifyInvalidParam(setPriority_priorityTooLowResponse, "priority", "too low");
    }

    private static void updatePriority_priorityTooHigh(String accessToken, UUID planetId, QueueResponse queueResponse) {
        Response setPriority_priorityTooHighResponse = SkyXplorePlanetQueueActions.getSetPriorityResponse(getServerPort(), accessToken, planetId, queueResponse.getType(), queueResponse.getItemId(), 11);

        ResponseValidator.verifyInvalidParam(setPriority_priorityTooHighResponse, "priority", "too high");
    }

    private QueueResponse updatePriority(String accessToken, UUID planetId, QueueResponse queueResponse) {
        SkyXplorePlanetQueueActions.setPriority(getServerPort(), accessToken, planetId, queueResponse.getType(), queueResponse.getItemId(), 7);

        queueResponse = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessToken, planetId)
            .getQueue()
            .get(0);

        assertThat(queueResponse.getOwnPriority()).isEqualTo(7);

        return queueResponse;
    }

    private static void cancelConstruction_invalidType(String accessToken, UUID planetId, QueueResponse queueResponse) {
        Response cancelConstruction_invalidTypeResponse = SkyXplorePlanetQueueActions.getCancelItemResponse(getServerPort(), accessToken, planetId, "asd", queueResponse.getItemId());

        ResponseValidator.verifyInvalidParam(cancelConstruction_invalidTypeResponse, "type", "invalid value");
    }

    private static void cancelConstruction(String accessToken, UUID planetId, QueueResponse queueResponse, UUID surfaceId) {
        SkyXplorePlanetQueueActions.cancelItem(getServerPort(), accessToken, planetId, queueResponse.getType(), queueResponse.getItemId());

        PlanetOverviewResponse planetOverviewResponse = SkyXplorePlanetActions.getPlanetOverview(getServerPort(), accessToken, planetId);

        assertThat(planetOverviewResponse.getQueue()).isEmpty();
        assertThat(SkyXplorePlanetActions.findSurfaceBySurfaceId(planetOverviewResponse.getSurfaces(), surfaceId).orElseThrow(() -> new RuntimeException("Surface not found")).getTerraformation()).isNull();
    }
}
