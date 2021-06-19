package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
class SendEventRequestValidator {
    void validate(SendEventRequest<?> sendEventRequest) {
        if (isNull(sendEventRequest.getMetadata())) {
            throw ExceptionFactory.invalidParam("metadata", "must not be null");
        }

        if (isBlank(sendEventRequest.getEventName())) {
            throw ExceptionFactory.invalidParam("eventName", "must not be null or blank");
        }
    }
}
