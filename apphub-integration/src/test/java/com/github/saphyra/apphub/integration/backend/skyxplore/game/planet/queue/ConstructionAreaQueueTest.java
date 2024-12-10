package com.github.saphyra.apphub.integration.backend.skyxplore.game.planet.queue;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreConstructionAreaActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.CustomAssertions;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.QueueResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;

public class ConstructionAreaQueueTest extends BackEndTest {

    @Test(groups = {"be", "skyxplore"})
    public void constructAndDeconstructConstructionAreaQueue() {
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

        SkyXploreConstructionAreaActions.constructConstructionArea(serverPort, accessTokenId, surfaceId, Constants.CONSTRUCTION_AREA_EXTRACTOR);

        AwaitilityWrapper.assertWithWaitList(() -> SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId))
            .returns(Constants.QUEUE_TYPE_CONSTRUCT_CONSTRUCTION_AREA, QueueResponse::getType)
            .extracting(queueResponse -> queueResponse.getData().get("dataId"))
            .isEqualTo(Constants.CONSTRUCTION_AREA_EXTRACTOR);

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);

        UUID constructionAreaId = AwaitilityWrapper.getWithWait(
                () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getConstructionArea(),
                constructionArea -> isNull(constructionArea.getConstruction()),
                120,
                5
            )
            .orElseThrow(() -> new RuntimeException("ConstructionArea is not constructed."))
            .getConstructionAreaId();

        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);

        SkyXploreConstructionAreaActions.deconstructConstructionArea(serverPort, accessTokenId, constructionAreaId);

        CustomAssertions.singleListAssertThat(SkyXplorePlanetActions.getQueue(serverPort, accessTokenId, planetId))
            .returns(Constants.QUEUE_TYPE_DECONSTRUCT_CONSTRUCTION_AREA, QueueResponse::getType)
            .extracting(queueResponse -> queueResponse.getData().get("dataId"))
            .isEqualTo(Constants.CONSTRUCTION_AREA_EXTRACTOR);
    }
}
