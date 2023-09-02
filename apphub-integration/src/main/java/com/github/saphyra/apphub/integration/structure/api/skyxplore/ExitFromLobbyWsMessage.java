package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ExitFromLobbyWsMessage {
    private UUID userId;
    private boolean host;
    private String characterName;
    private boolean expectedPlayer;
    private Long createdAt;
    private boolean onlyInvited;
}
