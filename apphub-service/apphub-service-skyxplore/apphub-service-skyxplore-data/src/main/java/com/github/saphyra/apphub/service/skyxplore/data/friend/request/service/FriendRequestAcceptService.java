package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendshipToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service.FriendshipFactory;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import com.github.saphyra.apphub.service.skyxplore.data.ws.SkyXploreFriendshipWebSocketHandler;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestAcceptService {
    private final FriendRequestDao friendRequestDao;
    private final FriendshipFactory friendshipFactory;
    private final FriendshipDao friendshipDao;
    private final SkyXploreFriendshipWebSocketHandler friendshipWebSocketHandler;
    private final FriendshipToResponseConverter friendshipToResponseConverter;

    @Transactional
    public FriendshipResponse accept(UUID userId, UUID friendRequestId) {
        FriendRequest friendRequest = friendRequestDao.findById(friendRequestId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "FriendRequest not found with id " + friendRequestId));

        if (!friendRequest.getFriendId().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " cannot accept friendRequest " + friendRequest);
        }

        Friendship friendship = friendshipFactory.create(friendRequest.getFriendId(), friendRequest.getSenderId());
        friendshipDao.save(friendship);
        friendRequestDao.delete(friendRequest);

        FriendshipResponse response = friendshipToResponseConverter.convert(friendship, userId);

        WsEventPayload payload = WsEventPayload.builder()
            .friendRequestId(friendRequestId)
            .friendship(friendshipToResponseConverter.convert(friendship, friendRequest.getSenderId()))
            .build();

        friendshipWebSocketHandler.sendEvent(friendRequest.getSenderId(), WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_ACCEPTED, payload);

        return response;
    }

    @Data
    @AllArgsConstructor
    @Builder
    static class WsEventPayload {
        private UUID friendRequestId;
        private FriendshipResponse friendship;
    }
}
