package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateNewGameService {
    private final SkyXploreLobbyWebSocketHandler skyXploreLobbyWebSocketHandler;
    private final SkyXploreGameCreationApiClient gameCreationClient;
    private final LocaleProvider localeProvider;
    private final AllianceSetupValidator allianceSetupValidator;

    void createNewGame(Lobby lobby) {
        Map<UUID, UUID> members = new HashMap<>();
        lobby.getMembers()
            .values()
            .forEach(member -> members.put(member.getUserId(), member.getAllianceId()));

        Map<UUID, String> alliances = lobby.getAlliances()
            .stream()
            .collect(Collectors.toMap(Alliance::getAllianceId, Alliance::getAllianceName));

        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(lobby.getHost())
            .members(members)
            .alliances(alliances)
            .settings(lobby.getSettings())
            .gameName(lobby.getLobbyName())
            .ais(lobby.getAis())
            .build();

        allianceSetupValidator.check(request);

        UUID gameId = gameCreationClient.createGame(request, localeProvider.getLocaleValidated());
        lobby.setGameCreationStarted(true);
        lobby.setGameId(gameId);

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .build();

        skyXploreLobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), event);
    }
}
