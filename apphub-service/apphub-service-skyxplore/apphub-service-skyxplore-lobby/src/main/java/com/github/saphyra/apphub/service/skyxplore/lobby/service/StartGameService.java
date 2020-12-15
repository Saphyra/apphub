package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameCreationApiClient;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
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
//TODO unit test
public class StartGameService {
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;
    private final SkyXploreGameCreationApiClient gameCreationClient;
    private final LocaleProvider localeProvider;

    public void startGame(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        if (!lobby.getHost().equals(userId)) {
            throw new RuntimeException(userId + " must not start the game."); //TODO proper exception
        }

        boolean allReady = lobby.getMembers()
            .values()
            .stream()
            .allMatch(Member::isReady);
        if (!allReady) {
            throw new RuntimeException("There are member(s) not ready.");
        }

        lobby.setGameCreationStarted(true);

        Map<UUID, UUID> members = new HashMap<>();
        lobby.getMembers()
            .values()
            .forEach(member -> members.put(member.getUserId(), member.getAlliance()));

        Map<UUID, String> alliances = lobby.getAlliances()
            .stream()
            .collect(Collectors.toMap(Alliance::getAllianceId, Alliance::getAllianceName));

        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .universeSize(lobby.getSettings().getUniverseSize())
            .systemSize(lobby.getSettings().getSystemSize())
            .planetSize(lobby.getSettings().getPlanetSize())
            .aiPresence(lobby.getSettings().getAiPresence())
            .build();

        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .host(lobby.getHost())
            .members(members)
            .alliances(alliances)
            .settings(settings)
            .build();

        gameCreationClient.createGame(request, localeProvider.getLocaleValidated());

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
