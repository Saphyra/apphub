package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LobbyMemberQueryService {
    private final LobbyDao lobbyDao;
    private final LobbyMemberResponseConverter converter;

    public LobbyMembersResponse getMembers(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        List<LobbyMemberResponse> members = lobby.getMembers()
            .values()
            .stream()
            .filter(member -> !member.getUserId().equals(lobby.getHost()))
            .map(member -> converter.convertMember(member, lobby.getAlliances()))
            .collect(Collectors.toList());

        List<String> alliances = lobby.getAlliances()
            .stream()
            .map(Alliance::getAllianceName)
            .collect(Collectors.toList());

        return LobbyMembersResponse.builder()
            .host(converter.convertMember(lobby.getMembers().get(lobby.getHost()), lobby.getAlliances()))
            .members(members)
            .alliances(alliances)
            .build();
    }
}
