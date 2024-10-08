package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreBuildingActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.PlanetOverviewResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.UUID;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class PauseGameTest extends BackEndTest {
    private static final String GAME_NAME = "game-name";

    @Test(groups = {"be", "skyxplore"})
    public void pauseAndResumeGame() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), accessTokenId, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        ApphubWsClient gameWsClient = SkyXploreFlow.startGame(getServerPort(), GAME_NAME, new Player(accessTokenId, userId1))
            .get(accessTokenId);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(getServerPort(), accessTokenId)
            .getPlanetId();
        ApphubWsClient planetWsClient = ApphubWsClient.createSkyXploreGamePlanet(getServerPort(), accessTokenId, planetId);

        UUID surfaceId = SkyXploreSurfaceActions.findEmptySurfaceId(getServerPort(), accessTokenId, planetId, Constants.SURFACE_TYPE_FOREST);

        //Create construction
        planetWsClient.clearMessages();
        SkyXploreBuildingActions.constructNewBuilding(getServerPort(), accessTokenId, planetId, surfaceId, Constants.DATA_ID_CAMP);
        planetWsClient.awaitForEvent(
                WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED,
                webSocketEvent -> SkyXplorePlanetActions.findSurfaceBySurfaceId(
                        webSocketEvent.getPayloadAs(PlanetOverviewResponse.class).getSurfaces(),
                        surfaceId
                    )
                    .filter(surfaceResponse -> !isNull(surfaceResponse.getBuilding()))
                    .isPresent()
            )
            .orElseThrow(() -> new RuntimeException("Construction was not created."));

        //Resume game
        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(getServerPort(), accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        //Check if game is running
        assertThat(planetWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED)).isPresent();

        //Pause game
        SkyXploreGameActions.setPaused(getServerPort(), accessTokenId, true);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not paused"));

        //Check if game is not running
        planetWsClient.clearMessages();

        assertThat(planetWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED, 15)).isEmpty();

        //Resume game
        gameWsClient.clearMessages();
        SkyXploreGameActions.setPaused(getServerPort(), accessTokenId, false);
        gameWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> !Boolean.parseBoolean(webSocketEvent.getPayload().toString()))
            .orElseThrow(() -> new RuntimeException("Game is not started"));

        //Check if game is running again
        assertThat(planetWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLANET_MODIFIED)).isPresent();
    }
}
