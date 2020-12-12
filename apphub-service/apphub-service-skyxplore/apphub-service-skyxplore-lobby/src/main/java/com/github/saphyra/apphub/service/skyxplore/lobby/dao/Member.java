package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class Member {
    private final UUID userId;
    private boolean isReady;
    private boolean connected;
}
