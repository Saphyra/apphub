package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipQueryService {
    private final FriendshipDao friendshipDao;
    private final FriendshipToResponseConverter friendshipToResponseConverter;

    public List<FriendshipResponse> getFriendships(UUID userId) {
        return friendshipDao.getByUserIdOrFriendId(userId)
            .stream()
            .map(friendship -> friendshipToResponseConverter.convert(friendship, friendship.getOtherUserId(userId)))
            .collect(Collectors.toList());
    }
}
