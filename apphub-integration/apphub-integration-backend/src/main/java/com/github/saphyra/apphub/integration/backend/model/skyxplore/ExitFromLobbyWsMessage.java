package com.github.saphyra.apphub.integration.backend.model.skyxplore;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ExitFromLobbyWsMessage {
    private UUID userId;
    private boolean host;
    private String characterName;
}
