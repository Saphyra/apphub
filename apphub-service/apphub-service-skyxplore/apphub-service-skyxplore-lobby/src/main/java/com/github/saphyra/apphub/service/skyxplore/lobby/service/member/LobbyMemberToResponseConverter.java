package com.github.saphyra.apphub.service.skyxplore.lobby.service.member;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
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

    public LobbyMemberResponse convertMember(LobbyMember lobbyMember) {
        return LobbyMemberResponse.builder()
            .userId(lobbyMember.getUserId())
            .status(lobbyMember.getStatus())
            .characterName(characterProxy.getCharacter(lobbyMember.getUserId()).getName())
            .allianceId(lobbyMember.getAllianceId())
            .createdAt(dateTimeUtil.getCurrentTimeEpochMillis())
            .build();
    }

    public LobbyMemberResponse convertInvitation(Invitation invitation) {
        UUID userId = invitation.getCharacterId();

        return LobbyMemberResponse.builder()
            .userId(userId)
            .characterName(characterProxy.getCharacter(userId).getName())
            .status(LobbyMemberStatus.INVITED)
            .build();
    }
}
