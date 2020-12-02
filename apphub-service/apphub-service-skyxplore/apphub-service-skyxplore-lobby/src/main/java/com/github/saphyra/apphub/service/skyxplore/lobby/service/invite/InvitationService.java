package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.Message;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreCharacterDataApiClient;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class InvitationService {
    private final LobbyDao lobbyDao;
    private final InvitationFactory invitationFactory;
    private final DateTimeUtil dateTimeUtil;
    private final MessageSenderApiClient messageSenderApiClient;
    private final LocaleProvider localeProvider;
    private final SkyXploreCharacterDataApiClient characterClient;
    private final AccessTokenProvider accessTokenProvider;

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
            .senderName(characterClient.getCharacter(accessTokenProvider.getAsString(), localeProvider.getLocaleValidated()).getName())
            .build();
        Message message = Message.builder()
            .eventName("invitation")
            .payload(invitationMessage)
            .build();
        try {
            messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, friendId, message, localeProvider.getLocaleValidated());
        } catch (FeignException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                //TODO handle target not connected error
            }
            throw e;
        }
    }
}
