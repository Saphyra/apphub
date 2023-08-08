package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class OutgoingChatWsMessageForGame {
    private String room;
    private String message;
    private UUID senderId;
    private String senderName;
}
