package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreBuildingModuleActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

public class BuildingModuleQueueTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void constructAndDeconstructBuildingModuleQueue(){
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

        UUID buildingModuleId = SkyXploreBuildingModuleActions.constructBuildingModule(serverPort, accessTokenId, constructionAreaId, Constants.BUILDING_MODULE_HAMSTER_WHEEL)
            .get(0)
            .getBuildingModuleId();

        AwaitilityWrapper.assertWithWaitList(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId))
            .returns(Constants.QUEUE_TYPE_CONSTRUCT_BUILDING_MODULE, QueueResponse::getType)
            .extracting(queueResponse -> queueResponse.getData().get("dataId"))
            .isEqualTo(Constants.BUILDING_MODULE_HAMSTER_WHEEL);

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        AwaitilityWrapper.create(120, 5)
            .until(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId).isEmpty())
            .assertTrue("BuildingModule is not constructed.");

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);

        SkyXploreBuildingModuleActions.deconstructBuildingModule(serverPort, accessTokenId, buildingModuleId);

        AwaitilityWrapper.assertWithWaitList(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId))
            .returns(Constants.QUEUE_TYPE_DECONSTRUCT_BUILDING_MODULE, QueueResponse::getType)
            .extracting(queueResponse -> queueResponse.getData().get("dataId"))
            .isEqualTo(Constants.BUILDING_MODULE_HAMSTER_WHEEL);
    }
}
