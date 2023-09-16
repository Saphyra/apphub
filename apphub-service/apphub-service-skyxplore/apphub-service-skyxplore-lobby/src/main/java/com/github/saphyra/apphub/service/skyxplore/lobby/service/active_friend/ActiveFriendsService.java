package com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.ActiveFriendResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyInvitationWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyType;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreDataProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActiveFriendsService {
    private final SkyXploreDataProxy skyXploreDataProxy;
    private final LobbyDao lobbyDao;
    private final SkyXploreLobbyInvitationWebSocketHandler invitationWebSocketHandler;

    public List<ActiveFriendResponse> getActiveFriends(AccessTokenHeader accessTokenHeader) {
        Lobby lobby = lobbyDao.findByUserIdValidated(accessTokenHeader.getUserId());

        return skyXploreDataProxy.getFriends(accessTokenHeader)
            .stream()
            .filter(friendshipResponse -> invitationWebSocketHandler.isConnected(friendshipResponse.getFriendId()))
            .filter(friendshipResponse -> LobbyType.NEW_GAME == lobby.getType() || lobby.getExpectedPlayers().contains(friendshipResponse.getFriendId()))
            .map(friendshipResponse -> ActiveFriendResponse.builder()
                .friendName(friendshipResponse.getFriendName())
                .friendId(friendshipResponse.getFriendId())
                .build()
            )
            .collect(Collectors.toList());
    }
}
