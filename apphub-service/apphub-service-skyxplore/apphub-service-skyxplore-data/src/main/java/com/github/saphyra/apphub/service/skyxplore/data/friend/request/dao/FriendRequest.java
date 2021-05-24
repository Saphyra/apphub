package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@Builder
public class FriendRequest {
    private final UUID friendRequestId;
    private final UUID senderId;
    private final UUID friendId;
}
