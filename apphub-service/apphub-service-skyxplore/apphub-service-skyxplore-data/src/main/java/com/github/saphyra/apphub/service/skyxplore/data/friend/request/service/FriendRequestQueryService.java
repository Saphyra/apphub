package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.SentFriendRequestResponse;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendRequestToResponseConverter;
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
public class FriendRequestQueryService {
    private final FriendRequestToResponseConverter friendRequestToResponseConverter;
    private final FriendRequestDao friendRequestDao;

    public List<SentFriendRequestResponse> getSentFriendRequests(UUID userId) {
        return friendRequestDao.getBySenderId(userId)
            .stream()
            .map(friendRequestToResponseConverter::toSentFriendRequest)
            .collect(Collectors.toList());
    }

    public List<IncomingFriendRequestResponse> getIncomingFriendRequests(UUID userId) {
        return friendRequestDao.getByFriendId(userId)
            .stream()
            .map(friendRequestToResponseConverter::toIncomingFriendRequest)
            .collect(Collectors.toList());
    }
}
