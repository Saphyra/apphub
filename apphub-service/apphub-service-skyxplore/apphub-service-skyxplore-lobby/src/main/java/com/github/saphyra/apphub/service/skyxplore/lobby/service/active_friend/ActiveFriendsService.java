package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.response.ActiveFriendResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
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
//TODO unit test
public class ActiveFriendsService {
    private final ActiveUsersDao activeUsersDao;
    private final SkyXploreDataProxy skyXploreDataProxy;
    private final UserActiveNotificationService userActiveNotificationService;

    public List<ActiveFriendResponse> getActiveFriends(AccessTokenHeader accessTokenHeader) {
        return skyXploreDataProxy.getFriends(accessTokenHeader)
            .stream()
            .filter(friendshipResponse -> isActive(friendshipResponse.getFriendId()))
            .map(friendshipResponse -> ActiveFriendResponse.builder()
                .friendName(friendshipResponse.getFriendName())
                .friendId(friendshipResponse.getFriendId())
                .build()
            )
            .collect(Collectors.toList());
    }

    private boolean isActive(UUID friendId) {
        return activeUsersDao.isActive(friendId);
    }

    public void playerOnline(UUID userId) {
        activeUsersDao.userOnline(userId);
        userActiveNotificationService.sendEvent(userId, WebSocketEventName.SKYXPLORE_LOBBY_USER_ONLINE);
    }

    public void playerOffline(UUID userId) {
        activeUsersDao.userOffline(userId);
        userActiveNotificationService.sendEvent(userId, WebSocketEventName.SKYXPLORE_LOBBY_USER_OFFLINE);
    }
}
