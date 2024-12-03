package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.SurfaceResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PauseGameTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void pauseAndResumeGame() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        int serverPort = getServerPort();
        UUID accessTokenId = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(serverPort, GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(serverPort, accessTokenId, planetId);

        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId, planetId, Constants.SURFACE_TYPE_DESERT);

        //Create construction
        planetWsClient.clearMessages();
        SkyXploreSurfaceActions.terraform(serverPort, accessTokenId, planetId, surfaceId, Constants.SURFACE_TYPE_FOREST);

        AwaitilityWrapper.awaitAssert(
            () -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId),
            surfaceResponse -> assertThat(surfaceResponse)
                .extracting(SurfaceResponse::getTerraformation)
                .isNotNull()
        );

        //Resume game
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        //Check if game is running
        AwaitilityWrapper.create(120, 1)
            .until(() -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getTerraformation().getCurrentWorkPoints() > 0)
            .assertTrue("Terraformation work is not started.");

        //Pause game
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, true);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not paused"));

        //Check if game is not running
        int progress = SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId)
            .getTerraformation()
            .getCurrentWorkPoints();

        SleepUtil.sleep(10000);

        assertThat(SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getTerraformation().getCurrentWorkPoints()).isEqualTo(progress);

        //Resume game
        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(serverPort, accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        //Check if game is running again
        AwaitilityWrapper.createDefault()
            .until(() -> SkyXplorePlanetActions.findSurfaceBySurfaceId(serverPort, accessTokenId, planetId, surfaceId).getTerraformation().getCurrentWorkPoints() > progress)
            .assertTrue("Progress is not increased.");
    }
}
