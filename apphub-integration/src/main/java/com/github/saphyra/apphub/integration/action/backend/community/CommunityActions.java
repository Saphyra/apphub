package com.github.saphyra.apphub.integration.action.backend.community;

import com.github.saphyra.apphub.integration.localization.Language;

import java.util.UUID;

public class CommunityActions {
    public static UUID setUpFriendship(Language language, UUID accessTokenId1, UUID accessTokenId2, UUID userId) {
        UUID friendRequestId = FriendRequestActions.createFriendRequest(language, accessTokenId1, userId)
            .getFriendRequestId();
        return FriendRequestActions.acceptFriendRequest(language, accessTokenId2, friendRequestId)
            .getFriendshipId();
    }
}
