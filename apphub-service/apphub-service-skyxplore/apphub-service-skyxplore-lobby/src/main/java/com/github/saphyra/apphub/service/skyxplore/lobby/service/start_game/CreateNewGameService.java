package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
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
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;
    private final SkyXploreGameCreationApiClient gameCreationClient;
    private final LocaleProvider localeProvider;

    void createNewGame(Lobby lobby) {
        Map<UUID, UUID> members = new HashMap<>();
        lobby.getMembers()
            .values()
            .forEach(member -> members.put(member.getUserId(), member.getAlliance()));

        Map<UUID, String> alliances = lobby.getAlliances()
            .stream()
            .collect(Collectors.toMap(Alliance::getAllianceId, Alliance::getAllianceName));

        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .universeSize(lobby.getSettings().getUniverseSize())
            .systemAmount(lobby.getSettings().getSystemAmount())
            .systemSize(lobby.getSettings().getSystemSize())
            .planetSize(lobby.getSettings().getPlanetSize())
            .aiPresence(lobby.getSettings().getAiPresence())
            .build();

        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(lobby.getHost())
            .members(members)
            .alliances(alliances)
            .settings(settings)
            .gameName(lobby.getLobbyName())
            .build();

        gameCreationClient.createGame(request, localeProvider.getLocaleValidated());
        lobby.setGameCreationStarted(true);

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(lobby.getMembers().keySet())
            .event(event)
            .build();
        messageSenderProxy.sendToLobby(message);

        lobbyDao.delete(lobby);
    }
}