package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
class RegisterProcessorRequestValidator {
    void validate(RegisterProcessorRequest request) {
        if (isBlank(request.getHost())) {
            throw ExceptionFactory.invalidParam("serviceName", "must not be null or blank");
        }

        if (isBlank(request.getEventName())) {
            throw ExceptionFactory.invalidParam("eventName", "must not be null or blank");
        }

        if (isNull(request.getUrl())) {
            throw ExceptionFactory.invalidParam("url", "must not be null");
        }
    }
}
