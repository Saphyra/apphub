package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
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
    private final FriendRequestDao friendRequestDao;
    private final FriendRequestToResponseConverter friendRequestToResponseConverter;

    public List<FriendRequestResponse> getSentFriendRequests(UUID userId) {
        return friendRequestDao.getBySenderId(userId)
            .stream()
            .map(friendRequest -> friendRequestToResponseConverter.convert(friendRequest, friendRequest.getReceiverId()))
            .collect(Collectors.toList());
    }

    public List<FriendRequestResponse> getReceivedFriendRequests(UUID userId) {
        return friendRequestDao.getByReceiverId(userId)
            .stream()
            .map(friendRequest -> friendRequestToResponseConverter.convert(friendRequest, friendRequest.getReceiverId()))
            .collect(Collectors.toList());
    }
}
