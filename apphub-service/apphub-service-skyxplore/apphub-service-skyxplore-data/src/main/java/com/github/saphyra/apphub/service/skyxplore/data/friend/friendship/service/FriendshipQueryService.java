package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.api.skyxplore.response.FriendshipResponse;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
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
//TODO unit test
public class FriendshipQueryService {
    private final FriendshipDao friendshipDao;
    private final CharacterDao characterDao;

    public List<FriendshipResponse> getFriends(UUID userId) {
        return friendshipDao.getByFriendId(userId)
            .stream()
            .map(friendship -> FriendshipResponse.builder()
                .friendshipId(friendship.getFriendshipId())
                .friendName(getFriendName(friendship, userId))
                .build())
            .collect(Collectors.toList());
    }

    private String getFriendName(Friendship friendship, UUID userId) {
        UUID friendId = friendship.getFriend1().equals(userId) ? friendship.getFriend2() : friendship.getFriend1();
        return characterDao.findByIdValidated(friendId).getName();
    }
}
