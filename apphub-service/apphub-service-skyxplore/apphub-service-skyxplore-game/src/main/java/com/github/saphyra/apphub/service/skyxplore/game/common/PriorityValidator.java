package com.github.saphyra.apphub.service.skyxplore.game.common;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityValidator {
    public void validate(Integer priority) {
        if (isNull(priority)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priority", "must not be null"), "Priority must not be null");
        }

        if (priority < 1) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priority", "too low"), "Priority too low");
        }

        if (priority > 10) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "priority", "too high"), "Priority too high");
        }
    }
}
