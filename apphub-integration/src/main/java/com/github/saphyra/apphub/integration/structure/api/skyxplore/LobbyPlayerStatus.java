package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LobbyPlayerStatus {
    READY("skyxplore-lobby-player-status-ready"),
    NOT_READY("skyxplore-lobby-player-status-not_ready"),
    INVITED("skyxplore-lobby-player-status-invited"),
    DISCONNECTED("skyxplore-lobby-player-status-disconnected"),
    ;

    @Getter
    private final String className;
}
