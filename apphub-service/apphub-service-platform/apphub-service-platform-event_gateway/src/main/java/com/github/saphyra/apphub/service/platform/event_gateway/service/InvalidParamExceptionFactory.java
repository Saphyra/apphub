package com.github.saphyra.apphub.service.platform.event_gateway.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.RestException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InvalidParamExceptionFactory {
    private static final String INVALID_FIELD_MESSAGE = "Invalid parameter";
    private static final String INVALID_PARAMETER_ERROR_CODE = "INVALID_PARAM";
    private static final String INVALID_PARAMETER_MESSAGE_FORMAT = "Invalid parameter: %s";

    public static RestException createException(String invalidParam) {
        Map<String, String> params = new HashMap<>();
        params.put(invalidParam, INVALID_FIELD_MESSAGE);
        ErrorMessage errorMessage = new ErrorMessage(INVALID_PARAMETER_ERROR_CODE, params);
        return new BadRequestException(errorMessage, String.format(INVALID_PARAMETER_MESSAGE_FORMAT, invalidParam));
    }
}
