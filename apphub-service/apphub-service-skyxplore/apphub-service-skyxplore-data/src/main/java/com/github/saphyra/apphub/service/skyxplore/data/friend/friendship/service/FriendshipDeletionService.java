package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.FriendshipDeletionPlayerProcessor;
import com.github.saphyra.apphub.service.skyxplore.data.ws.SkyXploreFriendshipWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDeletionService {
    private final FriendshipDao friendshipDao;
    private final SkyXploreFriendshipWebSocketHandler friendshipWebSocketHandler;
    private final FriendshipDeletionPlayerProcessor friendshipDeletionPlayerProcessor;

    public void removeFriendship(UUID friendshipId, UUID userId) {
        Friendship friendship = friendshipDao.findById(friendshipId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.FRIENDSHIP_NOT_FOUND, "Friendshhip not found with id " + friendshipId));

        if (!friendship.getFriend1().equals(userId) && !friendship.getFriend2().equals(userId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " cannot remove friendship " + friendshipId);
        }

        friendshipDao.delete(friendship);
        friendshipDeletionPlayerProcessor.processDeletedFriendship(friendship.getFriend1(), friendship.getFriend2());

        friendshipWebSocketHandler.sendEvent(friendship.getOtherId(userId), WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED, friendshipId);
    }
}
