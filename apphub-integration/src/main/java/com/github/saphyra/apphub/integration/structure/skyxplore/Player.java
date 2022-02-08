package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.Data;

import java.util.UUID;

@Data
public class Player {
    private final UUID accessTokenId;
    private final UUID userId;
}