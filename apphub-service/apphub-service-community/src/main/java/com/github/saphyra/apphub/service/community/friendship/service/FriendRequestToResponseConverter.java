package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.api.user.model.account.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendRequestToResponseConverter {
    private final AccountClientProxy accountClientProxy;

    FriendRequestResponse convert(FriendRequest friendRequest, UUID userIdToConvert) {
        AccountResponse accountResponse = accountClientProxy.getAccount(userIdToConvert);

        return FriendRequestResponse.builder()
            .friendRequestId(friendRequest.getFriendRequestId())
            .username(accountResponse.getUsername())
            .email(accountResponse.getEmail())
            .build();
    }
}
