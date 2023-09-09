package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
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
//TODO unit test
public class UserActiveNotificationService {
    private final SkyXploreDataProxy skyXploreDataProxy;
    private final ApplicationContextProxy applicationContextProxy;
    private final CharacterProxy characterProxy;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    public void userOnline(UUID userId) {
        lobbyWebSocketHandler.sendEvent(getRecipients(userId), createEvent(WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE, userId));;
    }

    public void userOffline(UUID userId) {
        lobbyWebSocketHandler.sendEvent(getRecipients(userId), createEvent(WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE, userId));;
    }

    private List<UUID> getRecipients(UUID userId) {
        AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
            .userId(userId)
            .roles(Arrays.asList("SKYXPLORE", "ACCESS"))
            .build();
        return skyXploreDataProxy.getFriends(accessTokenHeader)
            .stream()
            .map(FriendshipResponse::getFriendId)
            .filter(this::isInLobby)
            .collect(Collectors.toList());
    }

    private boolean isInLobby(UUID friendId) {
        return applicationContextProxy.getBean(LobbyDao.class)
            .findByUserId(friendId)
            .isPresent();
    }

    private WebSocketEvent createEvent(WebSocketEventName eventName, UUID friendId){
        return WebSocketEvent.builder()
            .eventName(eventName)
            .payload(ActiveFriendResponse.builder()
                .friendId(friendId)
                .friendName(characterProxy.getCharacter(friendId).getName())
                .build())
            .build();
    }
}
