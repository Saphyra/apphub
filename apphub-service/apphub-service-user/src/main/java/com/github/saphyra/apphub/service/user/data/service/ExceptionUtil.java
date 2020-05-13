package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import com.github.saphyra.apphub.lib.error_handler.exception.RestException;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ExceptionUtil {
    public RestException wrongPayloadException(String fieldName) {
        return wrongPayloadException(fieldName, "must not be null");
    }

    public RestException wrongPayloadException(String fieldName, String message) {
        Map<String, String> params = new HashMap<>();
        params.put(fieldName, message);
        return new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), params), String.format("%s is null.", fieldName));
    }

    public RestException invalidParamException(ErrorCode errorCode, String logMessage) {
        return new BadRequestException(new ErrorMessage(errorCode.name()), logMessage);
    }
}
