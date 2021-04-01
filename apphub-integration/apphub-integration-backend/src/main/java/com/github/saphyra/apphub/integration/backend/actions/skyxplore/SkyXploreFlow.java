package com.github.saphyra.apphub.integration.backend.actions.skyxplore;

import com.github.saphyra.apphub.integration.backend.model.skyxplore.Player;
import com.github.saphyra.apphub.integration.backend.model.skyxplore.ReadinessEvent;
import com.github.saphyra.apphub.integration.backend.ws.ApphubWsClient;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;

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
            .collect(Collectors.toList());

        WebSocketEvent readyEvent = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS)
            .payload(true)
            .build();

        hostLobbyWsClient.send(readyEvent);

        boolean allPlayersReady = Stream.concat(
            Stream.of(host),
            Arrays.stream(members)
        )
            .map(Player::getUserId)
            .allMatch(userId -> hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS, event -> event.getPayloadAs(ReadinessEvent.class).equals(new ReadinessEvent(userId, true))).isPresent());
        assertThat(allPlayersReady).isTrue();

        memberLobbyWsClients.forEach(skyXploreLobbyWsClient -> skyXploreLobbyWsClient.send(readyEvent));

        SkyXploreLobbyActions.startGame(language, host.getAccessTokenId());
        hostLobbyWsClient.awaitForEvent(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);

        return Stream.concat(Stream.of(host), Arrays.stream(members))
            .map(Player::getAccessTokenId)
            .collect(Collectors.toMap(Function.identity(), accessTokenId -> ApphubWsClient.createSkyXploreGame(language, accessTokenId)));
    }
}
