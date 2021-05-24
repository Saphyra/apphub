package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Alliance {
    private final UUID allianceId;
    private final String allianceName;
}
