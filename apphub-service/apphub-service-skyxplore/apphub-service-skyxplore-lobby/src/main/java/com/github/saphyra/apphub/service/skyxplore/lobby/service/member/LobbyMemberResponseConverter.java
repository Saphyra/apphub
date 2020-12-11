package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.LobbyMemberResponse;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class LobbyMemberResponseConverter {
    private final CharacterProxy characterProxy;

    LobbyMemberResponse convertMember(Member member) {
        return LobbyMemberResponse.builder()
            .userId(member.getUserId())
            .ready(member.isReady())
            .characterName(characterProxy.getCharacter(member.getUserId()).getName())
            .build();
    }
}
