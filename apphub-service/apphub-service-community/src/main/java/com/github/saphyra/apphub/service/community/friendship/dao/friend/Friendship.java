package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Friendship {
    private final UUID friendshipId;
    private final UUID userId;
    private final UUID friendId;
}
