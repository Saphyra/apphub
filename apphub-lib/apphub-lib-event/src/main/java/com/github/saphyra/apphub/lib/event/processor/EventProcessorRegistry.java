package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;

import java.util.List;

public interface EventProcessorRegistry {
    List<RegisterProcessorRequest> getRequests();
}
