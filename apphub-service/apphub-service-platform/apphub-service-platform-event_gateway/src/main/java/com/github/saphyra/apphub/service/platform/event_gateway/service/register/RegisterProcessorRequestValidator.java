package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
class RegisterProcessorRequestValidator {
    void validate(RegisterProcessorRequest request) {
        if (isBlank(request.getServiceName())) {
            throw new IllegalArgumentException("ServiceName is null.");
        }

        if (isBlank(request.getEventName())) {
            throw new IllegalArgumentException("EventName is null.");
        }

        if (isBlank(request.getUrl())) {
            throw new IllegalArgumentException("Url is null.");
        }
    }
}
