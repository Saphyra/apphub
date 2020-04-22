package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.platform.event_gateway.service.InvalidParamExceptionFactory.createException;
import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
class SendEventRequestValidator {
    void validate(SendEventRequest<?> sendEventRequest) {
        if (isNull(sendEventRequest.getMetadata())) {
            throw createException("metadata");
        }

        if (isBlank(sendEventRequest.getEventName())) {
            throw createException("eventName");
        }
    }
}
