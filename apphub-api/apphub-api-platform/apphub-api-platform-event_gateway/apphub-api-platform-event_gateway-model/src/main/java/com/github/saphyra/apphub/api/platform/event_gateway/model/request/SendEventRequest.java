package com.github.saphyra.apphub.api.platform.event_gateway.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendEventRequest<T> {
    private String eventName;

    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();
    private T payload;
}
