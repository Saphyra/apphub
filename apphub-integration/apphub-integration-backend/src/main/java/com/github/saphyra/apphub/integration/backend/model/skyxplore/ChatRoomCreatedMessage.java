package com.github.saphyra.apphub.integration.backend.model.skyxplore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatRoomCreatedMessage {
    private String id;
    private String title;
}