package com.github.saphyra.apphub.integration.ws.model;


import com.github.saphyra.apphub.integration.core.TestBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WebSocketEvent {
    @NonNull
    private WebSocketEventName eventName;
    private Object payload;

    public <T> T getPayloadAs(Class<T> type) {
        return TestBase.OBJECT_MAPPER_WRAPPER.convertValue(payload, type);
    }
}
