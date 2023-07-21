package com.github.saphyra.apphub.integration.structure.api.community;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GroupInvitationType {
    FRIENDS("invitation-type-friends"), FRIENDS_OF_FRIENDS("invitation-type-friends-of-friends");

    private final String id;
}
