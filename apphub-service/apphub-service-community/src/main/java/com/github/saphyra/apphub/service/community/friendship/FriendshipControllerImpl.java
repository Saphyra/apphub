package com.github.saphyra.apphub.service.community.friendship;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.community.server.FriendshipController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.community.friendship.service.FriendshipDeletionService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendshipQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class FriendshipControllerImpl implements FriendshipController {
    private final FriendshipQueryService friendshipQueryService;
    private final FriendshipDeletionService friendshipDeletionService;

    @Override
    public List<FriendshipResponse> getFriendships(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his Friendships", accessTokenHeader.getUserId());
        return friendshipQueryService.getFriendships(accessTokenHeader.getUserId());
    }

    @Override
    public void delete(UUID friendshipId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete Friendship {}", accessTokenHeader.getUserId(), friendshipId);
        friendshipDeletionService.delete(accessTokenHeader.getUserId(), friendshipId);
    }
}
