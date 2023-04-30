package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveFriendsService {
    private final ActiveUsersDao activeUsersDao;
    private final SkyXploreDataProxy skyXploreDataProxy;
    private final UserActiveNotificationService userActiveNotificationService;
    private final LobbyDao lobbyDao;

    public List<ActiveFriendResponse> getActiveFriends(AccessTokenHeader accessTokenHeader) {
        Lobby lobby = lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId());

        return skyXploreDataProxy.getFriends(accessTokenHeader)
            .stream()
            .filter(friendshipResponse -> activeUsersDao.isOnline(friendshipResponse.getFriendId()))
            .filter(friendshipResponse -> LobbyType.NEW_GAME == lobby.getType() || lobby.getExpectedPlayers().contains(friendshipResponse.getFriendId()))
            .map(friendshipResponse -> ActiveFriendResponse.builder()
                .friendName(friendshipResponse.getFriendName())
                .friendId(friendshipResponse.getFriendId())
                .build()
            )
            .collect(Collectors.toList());
    }

    public void playerOnline(UUID userId) {
        activeUsersDao.playerOnline(userId);
        userActiveNotificationService.sendEvent(userId, WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE);
    }

    public void playerOffline(UUID userId) {
        activeUsersDao.playerOffline(userId);
        userActiveNotificationService.sendEvent(userId, WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE);
    }
}
