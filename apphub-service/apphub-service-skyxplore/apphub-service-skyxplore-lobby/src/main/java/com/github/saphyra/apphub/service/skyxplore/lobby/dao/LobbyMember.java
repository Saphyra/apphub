package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class LobbyMember {
    private final UUID userId;
    private LobbyMemberStatus status;
    private boolean connected;
    private UUID allianceId;
}
