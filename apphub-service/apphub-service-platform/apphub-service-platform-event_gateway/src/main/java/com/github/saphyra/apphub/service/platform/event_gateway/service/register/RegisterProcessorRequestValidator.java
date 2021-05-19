package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.service.platform.event_gateway.service.InvalidParamExceptionFactory.createException;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
class RegisterProcessorRequestValidator {
    void validate(RegisterProcessorRequest request) {
        if (isBlank(request.getServiceName())) {
            throw createException("serviceName");
        }

        if (isBlank(request.getEventName())) {
            throw createException("eventName");
        }

        if (isNull(request.getUrl())) {
            throw createException("url");
        }
    }
}
