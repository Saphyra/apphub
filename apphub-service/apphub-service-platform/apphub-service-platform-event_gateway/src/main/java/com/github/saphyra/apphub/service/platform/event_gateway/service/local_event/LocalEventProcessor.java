package com.github.saphyra.apphub.service.platform.event_gateway.service.local_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;

public interface LocalEventProcessor {
    boolean shouldProcess(String eventName);

    void process(SendEventRequest<?> request);
}
