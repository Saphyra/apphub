package com.github.saphyra.apphub.service.community.friendship.dao.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FriendRequest {
    private final UUID friendRequestId;
    private final UUID senderId;
    private final UUID receiverId;
}
