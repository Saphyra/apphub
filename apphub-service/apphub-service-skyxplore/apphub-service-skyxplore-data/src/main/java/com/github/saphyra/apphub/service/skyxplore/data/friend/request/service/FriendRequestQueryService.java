package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
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
public class FriendRequestQueryService {
    private final CharacterDao characterDao;
    private final FriendRequestDao friendRequestDao;

    public List<SentFriendRequestResponse> getSentFriendRequests(UUID userId) {
        return friendRequestDao.getBySenderId(userId)
            .stream()
            .map(friendRequest -> SentFriendRequestResponse.builder()
                .friendRequestId(friendRequest.getFriendRequestId())
                .friendName(characterDao.findByIdValidated(friendRequest.getFriendId()).getName())
                .build())
            .collect(Collectors.toList());
    }
}
