package com.github.saphyra.apphub.api.platform.message_sender.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WebSocketEvent {
    private WebSocketEventName eventName;
    private Object payload;
}
