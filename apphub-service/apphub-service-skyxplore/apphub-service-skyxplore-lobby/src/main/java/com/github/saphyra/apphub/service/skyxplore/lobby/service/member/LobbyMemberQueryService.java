package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
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
public class LobbyMemberQueryService {
    private final LobbyDao lobbyDao;
    private final LobbyMemberToResponseConverter converter;

    public List<LobbyMemberResponse> getMembers(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);
        List<LobbyMemberResponse> result = new ArrayList<>();

        lobby.getMembers()
            .values()
            .stream()
            .map(converter::convertMember)
            .forEach(result::add);

        lobby.getInvitations()
            .stream()
            .map(converter::convertInvitation)
            .forEach(result::add);

        return result;
    }
}
