package com.github.saphyra.apphub.integration.action.backend.skyxplore;


import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMemberResponse;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.LobbyMemberStatus;
import com.github.saphyra.apphub.integration.structure.api.skyxplore.Player;
import com.github.saphyra.apphub.integration.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import org.java_websocket.client.WebSocketClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SkyXploreFlow {
    public static Map<UUID, ApphubWsClient> startGame(Language language, String gameName, Player host, Player... members) {
        Arrays.stream(members)
            .forEach(player -> SkyXploreFriendActions.setUpFriendship(language, host.getAccessTokenId(), player.getAccessTokenId(), player.getUserId()));

        SkyXploreLobbyActions.createLobby(language, host.getAccessTokenId(), gameName);
        ApphubWsClient hostLobbyWsClient = ApphubWsClient.createSkyXploreLobby(language, host.getAccessTokenId());

        Arrays.stream(members)
            .forEach(player -> SkyXploreLobbyActions.inviteToLobby(language, host.getAccessTokenId(), player.getUserId()));

        Arrays.stream(members)
            .forEach(player -> SkyXploreLobbyActions.acceptInvitation(language, player.getAccessTokenId(), host.getUserId()));

        List<ApphubWsClient> memberLobbyWsClients = Arrays.stream(members)
            .map(player -> ApphubWsClient.createSkyXploreLobby(language, player.getAccessTokenId()))
            .toList();

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();

        hostLobbyWsClient.send(readyEvent);

        memberLobbyWsClients.forEach(skyXploreLobbyWsClient -> skyXploreLobbyWsClient.send(readyEvent));

        boolean allPlayersReady = Stream.concat(
                Stream.of(host),
                Arrays.stream(members)
            )
            .map(Player::getUserId)
            .allMatch(userId -> hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, event -> {
                LobbyMemberResponse response = event.getPayloadAs(LobbyMemberResponse.class);
                return response.getStatus() == LobbyMemberStatus.READY && response.getUserId().equals(userId);
            }).isPresent());
        assertThat(allPlayersReady).isTrue();

        SkyXploreLobbyActions.startGame(language, host.getAccessTokenId());
        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 60)
            .orElseThrow(() -> new RuntimeException("GameLoaded event not arrived."));
        memberLobbyWsClients.forEach(memberLobbyWsClient -> memberLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED, 60).orElseThrow(() -> new RuntimeException("GameLoaded event not arrived.")));

        Stream.concat(Stream.of(hostLobbyWsClient), memberLobbyWsClients.stream())
            .forEach(WebSocketClient::close);

        return Stream.concat(Stream.of(host), Arrays.stream(members))
            .map(Player::getAccessTokenId)
            .collect(Collectors.toMap(Function.identity(), accessTokenId -> ApphubWsClient.createSkyXploreGame(language, accessTokenId)));
    }
}
