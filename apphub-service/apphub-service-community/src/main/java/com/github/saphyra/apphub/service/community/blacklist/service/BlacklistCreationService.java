package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlacklistCreationService {
    private final AccountClientProxy accountClientProxy;
    private final BlacklistDao blacklistDao;
    private final BlacklistFactory blacklistFactory;
    private final BlacklistToResponseConverter blacklistToResponseConverter;
    private final FriendshipDao friendshipDao;
    private final FriendRequestDao friendRequestDao;

    @Transactional
    public BlacklistResponse create(UUID userId, UUID blockedUserId) {
        if (blacklistDao.findByUserIdOrBlockedUserId(userId, blockedUserId).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS, "There is already a blacklist between " + userId + " and " + blockedUserId);
        }

        if (!accountClientProxy.userExists(blockedUserId)) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + blockedUserId);
        }

        friendshipDao.deleteByUserIdAndFriendId(userId, blockedUserId);
        friendRequestDao.deleteBySenderIdAndReceiverId(userId, blockedUserId);

        Blacklist blacklist = blacklistFactory.create(userId, blockedUserId);

        blacklistDao.save(blacklist);

        return blacklistToResponseConverter.convert(blacklist);
    }
}
