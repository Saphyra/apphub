package com.github.saphyra.apphub.integration.backend.skyxplore.game;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreCharacterActions;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.SkyXploreFlow;
import com.github.saphyra.apphub.integration.action.backend.skyxplore.game.SkyXploreGameActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.SkyXploreCharacterModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

public class ConnectionLostTest extends BackEndTest {
    /**
     * Tests that when a player disconnects during an active game, the host receives pause and disconnection events.
     *
     * <ul>
     *   <li>host receives SKYXPLORE_GAME_PAUSED with true payload after player disconnect.</li>
     *   <li>host receives SKYXPLORE_GAME_PLAYER_DISCONNECTED event indicating the player left.</li>
     * </ul>
     */
    @Test(groups = {"be", "skyxplore"})
    public void playerDisconnected() {
        RegistrationParameters hostData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel hostCharacter = SkyXploreCharacterModel.valid();
        String hostAccessToken = IndexPageActions.registerAndLogin(getServerPort(), hostData);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), hostAccessToken, hostCharacter);
        UUID hostUserId = UserDynamoDbRepository.getUserIdByEmail(hostData.getEmail());

        RegistrationParameters playerData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel playerCharacter = SkyXploreCharacterModel.valid();
        String playerAccessToken = IndexPageActions.registerAndLogin(getServerPort(), playerData);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), playerAccessToken, playerCharacter);
        UUID playerUserId = UserDynamoDbRepository.getUserIdByEmail(playerData.getEmail());

        Map<String, ApphubWsClient> gameWsClients = SkyXploreFlow.startGame(getServerPort(), new Player(hostAccessToken, hostUserId), new Player(playerAccessToken, playerUserId));

        SkyXploreGameActions.setPaused(getServerPort(), hostAccessToken, false);

        gameWsClients.forEach((_, wsClient) -> wsClient.clearMessages());

        gameWsClients.get(playerAccessToken).close();

        gameWsClients.get(hostAccessToken).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> webSocketEvent.getPayloadAs(Boolean.class));
        gameWsClients.get(hostAccessToken).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PLAYER_DISCONNECTED);
    }

    /**
     * Tests that when the host disconnects during an active game, the remaining player receives a pause event.
     *
     * <ul>
     *   <li>player receives SKYXPLORE_GAME_PAUSED with true payload after host disconnect.</li>
     * </ul>
     */
    @Test(groups = {"be", "skyxplore"})
    public void hostDisconnected() {
        RegistrationParameters hostData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel hostCharacter = SkyXploreCharacterModel.valid();
        String hostAccessToken = IndexPageActions.registerAndLogin(getServerPort(), hostData);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), hostAccessToken, hostCharacter);
        UUID hostUserId = UserDynamoDbRepository.getUserIdByEmail(hostData.getEmail());

        RegistrationParameters playerData = RegistrationParameters.validParameters();
        SkyXploreCharacterModel playerCharacter = SkyXploreCharacterModel.valid();
        String playerAccessToken = IndexPageActions.registerAndLogin(getServerPort(), playerData);
        SkyXploreCharacterActions.createOrUpdateCharacter(getServerPort(), playerAccessToken, playerCharacter);
        UUID playerUserId = UserDynamoDbRepository.getUserIdByEmail(playerData.getEmail());

        Map<String, ApphubWsClient> gameWsClients = SkyXploreFlow.startGame(getServerPort(), new Player(hostAccessToken, hostUserId), new Player(playerAccessToken, playerUserId));

        SkyXploreGameActions.setPaused(getServerPort(), hostAccessToken, false);

        gameWsClients.forEach((_, wsClient) -> wsClient.clearMessages());

        gameWsClients.get(hostAccessToken).close();

        gameWsClients.get(playerAccessToken).awaitForEvent(WebSocketEventName.SKYXPLORE_GAME_PAUSED, webSocketEvent -> webSocketEvent.getPayloadAs(Boolean.class));
    }
}
