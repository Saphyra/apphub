package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendshipToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
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

    public List<FriendshipResponse> getFriends(UUID userId) {
        return friendshipDao.getByFriendId(userId)
            .stream()
            .map(friendship -> friendshipToResponseConverter.convert(friendship, userId))
            .collect(Collectors.toList());
    }
}
