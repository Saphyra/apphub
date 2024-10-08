package com.github.saphyra.apphub.integration.action.backend.skyxplore;


import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyPlayerStatus;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SkyXploreFlow {
    public static Map<UUID, ApphubWsClient> startGame(int serverPort, Player host, Player... players) {
        return startGame(serverPort, Constants.DEFAULT_GAME_NAME, host, players);
    }

    public static Map<UUID, ApphubWsClient> startGame(int serverPort, String gameName, Player host, Player... players) {
        Arrays.stream(players)
            .forEach(player -> SkyXploreFriendActions.setUpFriendship(serverPort, host.getAccessTokenId(), player.getAccessTokenId(), player.getUserId()));

        SkyXploreLobbyActions.createLobby(serverPort, host.getAccessTokenId(), gameName);
        ApphubWsClient hostLobbyWsClient = ApphubWsClient.createSkyXploreLobby(serverPort, host.getAccessTokenId(), "host");

        Arrays.stream(players)
            .forEach(player -> SkyXploreLobbyActions.inviteToLobby(serverPort, host.getAccessTokenId(), player.getUserId()));

        Arrays.stream(players)
            .forEach(player -> SkyXploreLobbyActions.acceptInvitation(serverPort, player.getAccessTokenId(), host.getUserId()));

        List<ApphubWsClient> playerLobbyWsClients = Arrays.stream(players)
            .map(player -> ApphubWsClient.createSkyXploreLobby(serverPort, player.getAccessTokenId(), player.getUserId()))
            .toList();

        hostLobbyWsClient.clearMessages();

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();

        hostLobbyWsClient.send(readyEvent);

        playerLobbyWsClients.forEach(skyXploreLobbyWsClient -> skyXploreLobbyWsClient.send(readyEvent));

        boolean allPlayersReady = Stream.concat(
                Stream.of(host),
                Arrays.stream(players)
            )
            .map(Player::getUserId)
            .allMatch(userId -> hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, event -> {
                LobbyPlayerResponse response = event.getPayloadAs(LobbyPlayerResponse.class);
                return response.getStatus() == LobbyPlayerStatus.READY && response.getUserId().equals(userId);
            }).isPresent());
        assertThat(allPlayersReady).isTrue();

        SkyXploreLobbyActions.startGame(serverPort, host.getAccessTokenId());
        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 60)
            .orElseThrow(() -> new RuntimeException("GameLoaded event not arrived."));
        playerLobbyWsClients.forEach(playerLobbyWsClient -> playerLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 60).orElseThrow(() -> new RuntimeException("GameLoaded event not arrived.")));

        Stream.concat(Stream.of(hostLobbyWsClient), playerLobbyWsClients.stream())
            .forEach(WebSocketClient::close);

        return Stream.concat(Stream.of(host), Arrays.stream(players))
            .collect(Collectors.toMap(Player::getAccessTokenId, player -> ApphubWsClient.createSkyXploreGameMain(serverPort, player.getAccessTokenId(), player.getUserId())));
    }
}
