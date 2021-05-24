package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Friendship {
    private final UUID friendshipId;
    private final UUID friend1;
    private final UUID friend2;
}
