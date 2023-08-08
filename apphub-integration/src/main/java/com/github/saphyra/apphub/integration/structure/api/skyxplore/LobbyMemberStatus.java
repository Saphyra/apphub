package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LobbyMemberStatus {
    READY("skyxplore-lobby-member-status-ready"),
    NOT_READY("skyxplore-lobby-member-status-not_ready"),
    INVITED("skyxplore-lobby-member-status-invited"),
    DISCONNECTED("skyxplore-lobby-member-status-disconnected"),
    ;

    @Getter
    private final String className;
}
