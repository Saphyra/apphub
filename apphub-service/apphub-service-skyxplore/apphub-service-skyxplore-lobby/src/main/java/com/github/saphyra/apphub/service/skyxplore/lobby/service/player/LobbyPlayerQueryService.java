package com.github.saphyra.apphub.service.skyxplore.lobby.service.player;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyPlayerQueryService {
    private final LobbyDao lobbyDao;
    private final LobbyPlayerToResponseConverter converter;

    public List<LobbyPlayerResponse> getPlayers(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);
        List<LobbyPlayerResponse> result = new ArrayList<>();

        lobby.getPlayers()
            .values()
            .stream()
            .map(converter::convertPlayer)
            .forEach(result::add);

        new ArrayList<>(lobby.getInvitations())
            .stream()
            .map(converter::convertInvitation)
            .forEach(result::add);

        return result;
    }
}
