package com.github.saphyra.apphub.integration.action.backend.community;

import java.util.UUID;

public class CommunityActions {
    public static UUID setUpFriendship(UUID accessTokenId1, UUID accessTokenId2, UUID userId) {
        UUID friendRequestId = FriendRequestActions.createFriendRequest(accessTokenId1, userId)
            .getFriendRequestId();
        return FriendRequestActions.acceptFriendRequest(accessTokenId2, friendRequestId)
            .getFriendshipId();
    }
}
