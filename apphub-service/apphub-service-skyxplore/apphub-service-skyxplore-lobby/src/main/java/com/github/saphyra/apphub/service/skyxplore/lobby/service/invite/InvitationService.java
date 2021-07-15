package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@Builder
public class InvitationService {
    private final LobbyDao lobbyDao;
    private final InvitationFactory invitationFactory;
    private final DateTimeUtil dateTimeUtil;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;
    private final SkyXploreDataProxy dataProxy;
    private final int floodingLimitSeconds;

    public InvitationService(
        LobbyDao lobbyDao,
        InvitationFactory invitationFactory,
        DateTimeUtil dateTimeUtil,
        CharacterProxy characterProxy,
        MessageSenderProxy messageSenderProxy,
        SkyXploreDataProxy dataProxy,
        @Value("${lobby.invitation.floodingLimitSeconds}") int floodingLimitSeconds
    ) {
        this.lobbyDao = lobbyDao;
        this.invitationFactory = invitationFactory;
        this.dateTimeUtil = dateTimeUtil;
        this.characterProxy = characterProxy;
        this.messageSenderProxy = messageSenderProxy;
        this.dataProxy = dataProxy;
        this.floodingLimitSeconds = floodingLimitSeconds;
    }

    public void invite(AccessTokenHeader accessTokenHeader, UUID friendId) {
        boolean friends = dataProxy.getFriends(accessTokenHeader)
            .stream()
            .anyMatch(friendshipResponse -> friendshipResponse.getFriendId().equals(friendId));
        if (!friends) {
            throw ExceptionFactory.notLoggedException(HttpStatus.PRECONDITION_FAILED, accessTokenHeader.getUserId() + " is not a friend of " + friendId);
        }

        Lobby lobby = lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId());

        Optional<Invitation> existingInvitation = lobby.getInvitations()
            .stream()
            .filter(invitation -> invitation.getCharacterId().equals(friendId))
            .filter(invitation -> invitation.getInvitorId().equals(accessTokenHeader.getUserId()))
            .max(Comparator.comparing(Invitation::getInvitationTime));

        if (existingInvitation.isPresent()) {
            Invitation invitation = existingInvitation.get();
            LocalDateTime localDateTime = dateTimeUtil.getCurrentDate()
                .minusSeconds(floodingLimitSeconds);
            if (invitation.getInvitationTime().isAfter(localDateTime)) {
                throw ExceptionFactory.notLoggedException(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_FREQUENT_INVITATIONS, accessTokenHeader.getUserId() + " cannot invite " + friendId + " again yet.");
            }
        }

        inviteDirectly(accessTokenHeader.getUserId(), friendId, lobby);
    }

    public void inviteDirectly(UUID senderId, UUID characterId, Lobby lobby) {
        if (LobbyType.LOAD_GAME == lobby.getType() && !lobby.getExpectedPlayers().contains(characterId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, characterId + " is not an expected member of LOAD_GAME lobby " + lobby.getLobbyId());
        }

        Invitation invitation = invitationFactory.create(senderId, characterId);
        lobby.getInvitations()
            .add(invitation);

        InvitationMessage invitationMessage = InvitationMessage.builder()
            .senderId(senderId)
            .senderName(characterProxy.getCharacter().getName())
            .build();
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .payload(invitationMessage)
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(Arrays.asList(characterId))
            .event(event)
            .build();
        messageSenderProxy.sendToMainMenu(message);
    }
}
