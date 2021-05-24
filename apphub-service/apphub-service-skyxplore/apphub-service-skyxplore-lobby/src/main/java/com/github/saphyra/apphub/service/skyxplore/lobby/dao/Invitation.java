package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Invitation {
    private final UUID characterId;
    private final LocalDateTime invitationTime;
}
