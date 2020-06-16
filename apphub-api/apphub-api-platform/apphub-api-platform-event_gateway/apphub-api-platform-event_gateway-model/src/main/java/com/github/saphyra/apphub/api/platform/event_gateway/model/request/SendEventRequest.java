package com.github.saphyra.apphub.api.platform.event_gateway.model.request;

import com.github.saphyra.apphub.lib.common_util.Constants;
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

    public SendEventRequest<T> blockingRequest(boolean b) {
        metadata.put(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST, String.valueOf(b));
        return this;
    }

    public boolean isBlockingRequest() {
        return Boolean.parseBoolean(metadata.getOrDefault(Constants.SEND_EVENT_REQUEST_METADATA_KEY_BLOCKING_REQUEST, "false"));
    }
}
