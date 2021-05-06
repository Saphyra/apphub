package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.response.ActiveFriendResponse;
import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class UserActiveNotificationService {
    private final SkyXploreDataProxy skyXploreDataProxy;
    private final LobbyDao lobbyDao;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    void sendEvent(UUID userId, WebSocketEventName eventName) {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .userId(userId)
            .roles(Arrays.asList("SKYXPLORE"))
            .build();
        List<UUID> recipients = skyXploreDataProxy.getFriends(accessTokenHeader)
            .stream()
            .map(FriendshipResponse::getFriendId)
            .filter(this::isInLobby)
            .collect(Collectors.toList());

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(eventName)
            .payload(ActiveFriendResponse.builder()
                .friendId(userId)
                .friendName(characterProxy.getCharacter(userId).getName())
                .build())
            .build();
        WebSocketMessage webSocketMessage = WebSocketMessage.builder()
            .recipients(recipients)
            .event(event)
            .build();
        messageSenderProxy.sendToLobby(webSocketMessage);
    }

    private boolean isInLobby(UUID friendId) {
        return lobbyDao.findByUserId(friendId)
            .isPresent();
    }
}
