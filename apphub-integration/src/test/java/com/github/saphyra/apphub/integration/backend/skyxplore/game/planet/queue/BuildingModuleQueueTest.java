package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreBuildingModuleActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetQueueActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BuildingModuleQueueTest extends BackEndTest {
    private static final Integer NEW_PRIORITY = 7;

    @Test(groups = {"be", "skyxplore"})
    public void constructAndDeconstructBuildingModuleQueue() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        int serverPort = getServerPort();
        UUID accessTokenId = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        SkyXploreFlow.startGame(serverPort, Constants.DEFAULT_GAME_NAME, new Player(accessTokenId, userId1));

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessTokenId)
            .getPlanetId();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);
        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessTokenId, planetId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);
        UUID constructionAreaId = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getConstructionArea()
            .getConstructionAreaId();

        //Create construction queue item
        SkyXploreBuildingModuleActions.constructBuildingModule(serverPort, accessTokenId, constructionAreaId, Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        QueueResponse queueResponse = AwaitilityWrapper.getSingleItemFromListWithWait(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId));

        assertThat(queueResponse)
            .returns(Constants.QUEUE_TYPE_CONSTRUCT_BUILDING_MODULE, QueueResponse::getType)
            .extracting(qr -> qr.getData().get("dataId"))
            .isEqualTo(Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        //Set priority of construction
        setPriority_unknownQueueItemType(serverPort, accessTokenId, planetId, queueResponse.getItemId());
        setPriority_tooLow(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());
        setPriority_tooHigh(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());
        setPriority(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        //Cancel construction
        SkyXplorePlanetQueueActions.cancelItem(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("Construction is not cancelled.");

        //Finish construction
        UUID buildingModuleId = SkyXploreBuildingModuleActions.constructBuildingModule(serverPort, accessTokenId, constructionAreaId, Constants.BUILDING_MODULE_HAMSTER_WHEEL)
            .get(0)
            .getBuildingModuleId();
        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("Construction is not started.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("BuildingModule is not constructed.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);

        //Create deconstruction queue item
        SkyXploreBuildingModuleActions.deconstructBuildingModule(serverPort, accessTokenId, buildingModuleId);

        queueResponse = AwaitilityWrapper.getSingleItemFromListWithWait(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId));

        assertThat(queueResponse)
            .returns(Constants.QUEUE_TYPE_DECONSTRUCT_BUILDING_MODULE, QueueResponse::getType)
            .extracting(qr -> qr.getData().get("dataId"))
            .isEqualTo(Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        //Set priority of deconstruction
        setPriority_unknownQueueItemType(serverPort, accessTokenId, planetId, queueResponse.getItemId());
        setPriority_tooLow(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());
        setPriority_tooHigh(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());
        setPriority(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        //Cancel deconstruction
        SkyXplorePlanetQueueActions.cancelItem(serverPort, accessTokenId, planetId, queueResponse.getType(), queueResponse.getItemId());

        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("Deconstruction is not cancelled.");

        //Finish deconstruction
        SkyXploreBuildingModuleActions.deconstructBuildingModule(serverPort, accessTokenId, buildingModuleId);
        AwaitilityWrapper.createDefault()
            .until(() -> !SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("Deconstruction is not started.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("BuildingModule is not deconstructed.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);
    }

    private void setPriority(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        SkyXplorePlanetQueueActions.setPriority(serverPort, accessTokenId, planetId, type, itemId, NEW_PRIORITY);

        AwaitilityWrapper.assertWithWaitList(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId))
            .returns(NEW_PRIORITY, QueueResponse::getOwnPriority);
    }

    private void setPriority_tooHigh(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        Response response = SkyXplorePlanetQueueActions.getSetPriorityResponse(serverPort, accessTokenId, planetId, type, itemId, 11);

        ResponseValidator.verifyInvalidParam(response, "priority", "too high");
    }

    private void setPriority_tooLow(int serverPort, UUID accessTokenId, UUID planetId, String type, UUID itemId) {
        Response response = SkyXplorePlanetQueueActions.getSetPriorityResponse(serverPort, accessTokenId, planetId, type, itemId, 0);

        ResponseValidator.verifyInvalidParam(response, "priority", "too low");
    }

    private void setPriority_unknownQueueItemType(int serverPort, UUID accessTokenId, UUID planetId, UUID itemId) {
        Response response = SkyXplorePlanetQueueActions.getSetPriorityResponse(serverPort, accessTokenId, planetId, "asd", itemId, NEW_PRIORITY);

        ResponseValidator.verifyInvalidParam(response, "type", "invalid value");
    }
}
