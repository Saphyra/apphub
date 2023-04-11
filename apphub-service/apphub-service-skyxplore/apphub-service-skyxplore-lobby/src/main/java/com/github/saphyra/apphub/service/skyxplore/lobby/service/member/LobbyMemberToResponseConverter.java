package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LobbyMemberToResponseConverter {
    private final CharacterProxy characterProxy;
    private final DateTimeUtil dateTimeUtil;

    public LobbyMemberResponse convertMember(Member member) {
        return LobbyMemberResponse.builder()
            .userId(member.getUserId())
            .status(member.getStatus())
            .characterName(characterProxy.getCharacter(member.getUserId()).getName())
            .allianceId(member.getAlliance())
            .createdAt(dateTimeUtil.getCurrentTimeEpochMillis())
            .build();
    }

    //TODO unit test
    public LobbyMemberResponse convertInvitation(Invitation invitation) {
        UUID userId = invitation.getCharacterId();

        return LobbyMemberResponse.builder()
            .userId(userId)
            .characterName(characterProxy.getCharacter(userId).getName())
            .status(LobbyMemberStatus.INVITED)
            .build();
    }
}
