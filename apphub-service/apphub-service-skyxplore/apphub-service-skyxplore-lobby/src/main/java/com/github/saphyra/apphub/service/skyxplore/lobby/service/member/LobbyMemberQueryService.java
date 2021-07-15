package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberStatus;
import com.github.saphyra.apphub.api.skyxplore.response.LobbyMembersResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyMemberQueryService {
    private final LobbyDao lobbyDao;
    private final LobbyMemberResponseConverter converter;
    private final CharacterProxy characterProxy;

    public LobbyMembersResponse getMembers(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        List<LobbyMemberResponse> members = lobby.getMembers()
            .values()
            .stream()
            .filter(member -> !member.getUserId().equals(lobby.getHost()))
            .map(member -> converter.convertMember(member, lobby.getAlliances()))
            .collect(Collectors.toList());

        lobby.getExpectedPlayers()
            .stream()
            .filter(uuid -> !lobby.getMembers().containsKey(uuid))
            .map(this::createInvitedMember)
            .forEach(members::add);

        List<String> alliances = lobby.getAlliances()
            .stream()
            .map(Alliance::getAllianceName)
            .collect(Collectors.toList());

        Member host = lobby.getMembers().get(lobby.getHost());
        LobbyMemberResponse hostResponse = converter.convertMember(host, lobby.getAlliances());
        return LobbyMembersResponse.builder()
            .host(hostResponse)
            .members(members)
            .alliances(alliances)
            .build();
    }

    private LobbyMemberResponse createInvitedMember(UUID userId) {
        return LobbyMemberResponse.builder()
            .userId(userId)
            .characterName(characterProxy.getCharacter(userId).getName())
            .status(LobbyMemberStatus.INVITED)
            .build();
    }
}
