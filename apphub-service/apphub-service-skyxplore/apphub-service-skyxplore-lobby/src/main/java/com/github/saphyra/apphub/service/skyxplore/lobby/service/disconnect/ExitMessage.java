package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
class ExitMessage {
    private UUID userId;
    private boolean host;
    private String characterName;
    private boolean expectedPlayer;
    private Long createdAt;
    private boolean onlyInvited;
}
