package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.PreconditionFailedException;
import com.github.saphyra.apphub.lib.exception.TooManyRequestsException;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
            throw new PreconditionFailedException(accessTokenHeader.getUserId() + " is not a friend of " + friendId);
        }

        Lobby lobby = lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId());

        Optional<Invitation> existingInvitation = lobby.getInvitations()
            .stream()
            .filter(invitation -> invitation.getCharacterId().equals(friendId))
            .max(Comparator.comparing(Invitation::getInvitationTime));

        if (existingInvitation.isPresent()) {
            Invitation invitation = existingInvitation.get();
            LocalDateTime localDateTime = dateTimeUtil.getCurrentDate()
                .minusSeconds(floodingLimitSeconds);
            if (invitation.getInvitationTime().isAfter(localDateTime)) {
                throw new TooManyRequestsException(new ErrorMessage(ErrorCode.TOO_FREQUENT_INVITATIONS.name()), accessTokenHeader.getUserId() + " cannot invite " + friendId + " again yet.");
            }
        }

        Invitation invitation = invitationFactory.create(friendId);
        lobby.getInvitations()
            .add(invitation);

        InvitationMessage invitationMessage = InvitationMessage.builder()
            .senderId(accessTokenHeader.getUserId())
            .senderName(characterProxy.getCharacter().getName())
            .build();
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_MAIN_MENU_INVITATION)
            .payload(invitationMessage)
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(Arrays.asList(friendId))
            .event(event
            )
            .build();
        messageSenderProxy.sendToMainMenu(message);
    }
}
