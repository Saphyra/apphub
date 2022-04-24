package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class FriendshipToResponseConverter {
    private final AccountClientProxy accountClientProxy;

    FriendshipResponse convert(Friendship friendship, UUID userIdToConvert) {
        AccountResponse accountResponse = accountClientProxy.getAccount(userIdToConvert);

        return FriendshipResponse.builder()
            .friendshipId(friendship.getFriendshipId())
            .username(accountResponse.getUsername())
            .email(accountResponse.getEmail())
            .build();
    }
}
