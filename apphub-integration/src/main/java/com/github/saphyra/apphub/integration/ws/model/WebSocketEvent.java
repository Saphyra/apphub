package com.github.saphyra.apphub.integration.ws.model;


import com.github.saphyra.apphub.integration.TestBase;
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

    public <T> T getPayloadAs(Class<T> type) {
        return TestBase.OBJECT_MAPPER_WRAPPER.convertValue(payload, type);
    }
}
