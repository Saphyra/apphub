package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Data;

import java.util.UUID;

@Data
public class Player {
    private final String accessToken;
    private final UUID userId;
}