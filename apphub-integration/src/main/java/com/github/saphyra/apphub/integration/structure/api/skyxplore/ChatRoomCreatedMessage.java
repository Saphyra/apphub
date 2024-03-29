package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomCreatedMessage {
    private String roomId;
    private String roomTitle;
}