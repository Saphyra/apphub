package com.github.saphyra.apphub.service.skyxplore.data.friend.converter;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipToResponseConverter {
    private final FriendNameQueryService friendNameQueryService;

    public FriendshipResponse convert(Friendship friendship, UUID userId) {
        return FriendshipResponse.builder()
            .friendshipId(friendship.getFriendshipId())
            .friendId(friendship.getOtherId(userId))
            .friendName(friendNameQueryService.getFriendName(friendship, userId))
            .build();
    }
}
