package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class JoinToLobbyService {
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    public void acceptInvitation(UUID userId, UUID invitorId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(invitorId);

        List<Invitation> invitations = lobby.getInvitations();
        Invitation invitation = invitations
            .stream()
            .filter(i -> i.getCharacterId().equals(userId))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " is not invited by " + invitorId));

        invitations.remove(invitation);

        Member member = Member.builder()
            .userId(userId)
            .status(LobbyMemberStatus.NOT_READY)
            .build();
        lobby.getMembers().put(userId, member);
    }

    public void userJoinedToLobby(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);
        Member member = lobby.getMembers().get(userId);
        member.setConnected(true);
        member.setStatus(LobbyMemberStatus.NOT_READY);

        LobbyMemberResponse response = lobbyMemberToResponseConverter.convertMember(member);
        messageSenderProxy.lobbyMemberModified(response, lobby.getMembers().keySet());
        messageSenderProxy.lobbyMemberConnected(response, lobby.getMembers().keySet());
    }
}
