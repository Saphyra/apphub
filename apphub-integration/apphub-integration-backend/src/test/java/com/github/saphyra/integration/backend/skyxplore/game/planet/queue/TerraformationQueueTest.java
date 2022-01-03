package com.github.saphyra.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.ResponseValidator;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.backend.actions.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TerraformationQueueTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(dataProvider = "languageDataProvider", groups = "skyxplore")
    public void constructionQueueCrud(Language language) {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(language, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(language, GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(language, accessTokenId)
            .getPlanetId();

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(language, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        SkyXploreSurfaceActions.terraform(language, accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_LAKE);

        //Get queue
        List<QueueResponse> queue = SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId);

        assertThat(queue).hasSize(1);
        QueueResponse queueResponse = queue.get(0);
        assertThat(queueResponse.getType()).isEqualTo(Constants.QUEUE_TYPE_TERRAFORMATION);
        assertThat(queueResponse.getOwnPriority()).isEqualTo(Constants.DEFAULT_PRIORITY);
        assertThat(queueResponse.getData()).containsEntry("currentSurfaceType", Constants.SURFACE_TYPE_DESERT);
        assertThat(queueResponse.getData()).containsEntry("targetSurfaceType", Constants.SURFACE_TYPE_LAKE);

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
        SkyXplorePlanetQueueActions.setPriority(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId(), 7);

        queueResponse = SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId)
            .get(0);

        assertThat(queueResponse.getOwnPriority()).isEqualTo(7);

        //Cancel construction - Invalid type
        Response cancelConstruction_invalidTypeResponse = SkyXplorePlanetQueueActions.getCancelItemResponse(language, accessTokenId, planetId, "asd", queueResponse.getItemId());

        ResponseValidator.verifyInvalidParam(language, cancelConstruction_invalidTypeResponse, "type", "invalid value");

        //Cancel construction
        SkyXplorePlanetQueueActions.cancelItem(language, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        assertThat(SkyXplorePlanetQueueActions.getQueue(language, accessTokenId, planetId)).isEmpty();
    }
}
