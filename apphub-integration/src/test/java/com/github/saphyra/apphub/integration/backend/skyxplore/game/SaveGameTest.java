package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreLobbyActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXplorePlanetActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreSavedGameActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSolarSystemActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreSurfaceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.game.ConstructionResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SaveGameTest extends BackEndTest {
    @Test(groups = {"be", "skyxplore"})
    public void saveGame() {
        RegistrationParameters userData1 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel1 = SkyXploreCharacterModel.valid();
        Integer serverPort = getServerPort();
        UUID accessTokenId1 = IndexPageActions.registerAndLogin(serverPort, userData1);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId1, characterModel1);
        UUID userId1 = DatabaseUtil.getUserIdByEmail(userData1.getEmail());

        RegistrationParameters userData2 = RegistrationParameters.validParameters();
        SkyXploreCharacterModel characterModel2 = SkyXploreCharacterModel.valid();
        UUID accessTokenId2 = IndexPageActions.registerAndLogin(serverPort, userData2);
        SkyXploreCharacterActions.createOrUpdateCharacter(serverPort, accessTokenId2, characterModel2);
        UUID userId2 = DatabaseUtil.getUserIdByEmail(userData2.getEmail());

        SkyXploreFlow.startGame(serverPort, new Player(accessTokenId1, userId1), new Player(accessTokenId2, userId2));

        notHost(accessTokenId2);

        UUID planetId = SkyXploreSolarSystemActions.getPopulatedPlanet(serverPort, accessTokenId1)
            .getPlanetId();
        UUID surfaceId = SkyXplorePlanetActions.findEmptySurface(serverPort, accessTokenId1, planetId, Constants.SURFACE_TYPE_DESERT);
        UUID gameId = AwaitilityWrapper.getListWithWait(() -> SkyXploreSavedGameActions.getSavedGames(serverPort, accessTokenId1), savedGameResponses -> !savedGameResponses.isEmpty())
            .get(0)
            .getGameId();

        exitWithoutSaving(accessTokenId1, planetId, surfaceId, gameId);
        exitAfterSaving(accessTokenId1, planetId, surfaceId, gameId);
    }

    private void exitAfterSaving(UUID hostAccessToken, UUID planetId, UUID surfaceId, UUID gameId) {
        SkyXploreSurfaceActions.terraform(getServerPort(), hostAccessToken, planetId, surfaceId, Constants.SURFACE_TYPE_CONCRETE);

        SkyXploreGameActions.saveGame(getServerPort(), hostAccessToken);
        SkyXploreGameActions.exit(getServerPort(), hostAccessToken);

        loadGame(hostAccessToken, gameId);

        ConstructionResponse terraformation = SkyXplorePlanetActions.findSurfaceBySurfaceId(SkyXplorePlanetActions.getSurfaces(getServerPort(), hostAccessToken, planetId), surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."))
            .getTerraformation();
        assertThat(terraformation).isNotNull();
    }

    private void exitWithoutSaving(UUID hostAccessToken, UUID planetId, UUID surfaceId, UUID gameId) {
        SkyXploreSurfaceActions.terraform(getServerPort(), hostAccessToken, planetId, surfaceId, Constants.SURFACE_TYPE_CONCRETE);

        SkyXploreGameActions.exit(getServerPort(), hostAccessToken);

        loadGame(hostAccessToken, gameId);

        ConstructionResponse terraformation = SkyXplorePlanetActions.findSurfaceBySurfaceId(SkyXplorePlanetActions.getSurfaces(getServerPort(), hostAccessToken, planetId), surfaceId)
            .orElseThrow(() -> new RuntimeException("Surface not found."))
            .getTerraformation();
        assertThat(terraformation).isNull();
    }

    private static void loadGame(UUID hostAccessToken, UUID gameId) {
        SkyXploreLobbyActions.loadGame(getServerPort(), hostAccessToken, gameId);
        ApphubWsClient lobbyWsClient = ApphubWsClient.createSkyXploreLobby(getServerPort(), hostAccessToken, "lobby");
        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();
        lobbyWsClient.send(readyEvent);
        SkyXploreLobbyActions.startGame(getServerPort(), hostAccessToken);

        lobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 60)
            .orElseThrow(() -> new RuntimeException("GameLoaded event not arrived."));
    }

    private static void notHost(UUID playerAccessToken) {
        Response response = SkyXploreGameActions.getSaveGameResponse(getServerPort(), playerAccessToken);
        ResponseValidator.verifyForbiddenOperation(response);
    }
}
