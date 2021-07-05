package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LobbyMemberResponseConverter {
    private final CharacterProxy characterProxy;

    LobbyMemberResponse convertMember(Member member, List<Alliance> alliances) {
        return LobbyMemberResponse.builder()
            .userId(member.getUserId())
            .status(member.getStatus())
            .characterName(characterProxy.getCharacter(member.getUserId()).getName())
            .alliance(getAllianceName(alliances, member.getAlliance()))
            .build();
    }

    private String getAllianceName(List<Alliance> alliances, UUID alliance) {
        return alliances.stream()
            .filter(a -> a.getAllianceId().equals(alliance))
            .findFirst()
            .map(Alliance::getAllianceName)
            .orElse(null);
    }
}
