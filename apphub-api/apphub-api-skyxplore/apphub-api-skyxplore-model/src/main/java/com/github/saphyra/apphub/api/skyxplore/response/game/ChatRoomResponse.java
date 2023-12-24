package com.github.saphyra.apphub.api.skyxplore.response.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatRoomResponse {
    private String roomId;
    private String roomTitle;
}
