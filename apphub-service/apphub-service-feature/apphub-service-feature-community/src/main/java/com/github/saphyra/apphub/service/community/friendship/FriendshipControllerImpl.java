package com.github.saphyra.apphub.service.community.friendship;

import com.github.saphyra.apphub.api.feature.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.feature.community.server.FriendshipController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
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
    public List<FriendshipResponse> getFriendships(AccessToken accessToken) {
        log.info("{} wants to know his Friendships", accessToken.getUserId());
        return friendshipQueryService.getFriendships(accessToken.getUserId());
    }

    @Override
    public void delete(UUID friendshipId, AccessToken accessToken) {
        log.info("{} wants to delete Friendship {}", accessToken.getUserId(), friendshipId);
        friendshipDeletionService.delete(accessToken.getUserId(), friendshipId);
    }
}
