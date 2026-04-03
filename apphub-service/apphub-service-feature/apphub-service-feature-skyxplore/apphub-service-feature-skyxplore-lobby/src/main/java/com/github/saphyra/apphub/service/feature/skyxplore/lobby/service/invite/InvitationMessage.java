package com.github.saphyra.apphub.service.feature.skyxplore.lobby.service.invite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class InvitationMessage {
    private final UUID senderId;
    private final String senderName;
}
