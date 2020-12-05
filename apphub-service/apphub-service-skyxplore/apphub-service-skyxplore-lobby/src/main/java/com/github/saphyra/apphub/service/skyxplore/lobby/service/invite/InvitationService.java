package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import static com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEvents.INVITATION;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class InvitationService {
    private final LobbyDao lobbyDao;
    private final InvitationFactory invitationFactory;
    private final DateTimeUtil dateTimeUtil;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    @Value("${lobby.invitation.floodingLimitSeconds}")
    private int floodingLimitSeconds;

    public void invite(UUID userId, UUID friendId) {
        //TODO check if players are friends
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        Optional<Invitation> existingInvitation = lobby.getInvitations()
            .stream()
            .filter(invitation -> invitation.getCharacterId().equals(friendId))
            .max(Comparator.comparing(Invitation::getInvitationTime));

        if (existingInvitation.isPresent()) {
            Invitation invitation = existingInvitation.get();
            LocalDateTime localDateTime = dateTimeUtil.getCurrentDate()
                .minusSeconds(floodingLimitSeconds);
            if (invitation.getInvitationTime().isAfter(localDateTime)) {
                throw new RuntimeException(); //TODO proper exception
            }
        }

        Invitation invitation = invitationFactory.create(friendId);
        lobby.getInvitations()
            .add(invitation);

        InvitationMessage invitationMessage = InvitationMessage.builder()
            .senderId(userId)
            .senderName(characterProxy.getCharacter().getName())
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(Arrays.asList(friendId))
            .event(WebSocketEvent.builder()
                .eventName(INVITATION)
                .payload(invitationMessage)
                .build()
            )
            .build();
        messageSenderProxy.sendToMainMenu(message);
    }
}
