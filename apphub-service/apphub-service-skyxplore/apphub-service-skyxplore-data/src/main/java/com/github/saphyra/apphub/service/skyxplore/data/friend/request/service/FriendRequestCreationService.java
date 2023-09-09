package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.SentFriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendRequestToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import com.github.saphyra.apphub.service.skyxplore.data.ws.SkyXploreFriendshipWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendRequestCreationService {
    private final FriendshipDao friendshipDao;
    private final FriendRequestDao friendRequestDao;
    private final CharacterDao characterDao;
    private final FriendRequestFactory friendRequestFactory;
    private final SkyXploreFriendshipWebSocketHandler friendshipWebSocketHandler;
    private final FriendRequestToResponseConverter friendRequestToResponseConverter;

    public SentFriendRequestResponse createFriendRequest(UUID senderId, UUID friendId) {
        if (characterDao.findById(friendId).isEmpty()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "Character not found with id " + friendId);
        }

        if (friendRequestDao.findBySenderIdAndFriendId(senderId, friendId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_EXISTS, String.format("FriendRequest already exists for sender %s and friendCandidate %s", senderId, friendId));
        }

        if (friendshipDao.findByFriendIds(senderId, friendId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.FRIENDSHIP_ALREADY_EXISTS, String.format("Friendship already exists for sender %s and friendCandidate %s", senderId, friendId));
        }

        FriendRequest friendRequest = friendRequestFactory.create(senderId, friendId);
        friendRequestDao.save(friendRequest);

        IncomingFriendRequestResponse friendRequestResponse = friendRequestToResponseConverter.toIncomingFriendRequest(friendRequest);

        friendshipWebSocketHandler.sendEvent(friendId, WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_SENT, friendRequestResponse);

        return friendRequestToResponseConverter.toSentFriendRequest(friendRequest);
    }
}
