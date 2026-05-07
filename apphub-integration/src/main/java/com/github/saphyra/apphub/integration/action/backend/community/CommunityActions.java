package com.github.saphyra.apphub.integration.action.backend.community;

import java.util.UUID;

public class CommunityActions {
    public static UUID setUpFriendship(int serverPort, String accessToken1, String accessToken2, UUID userId) {
        UUID friendRequestId = FriendRequestActions.createFriendRequest(serverPort, accessToken1, userId)
            .getFriendRequestId();
        return FriendRequestActions.acceptFriendRequest(serverPort, accessToken2, friendRequestId)
            .getFriendshipId();
    }
}
