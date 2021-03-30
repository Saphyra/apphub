package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

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
class AllianceNameValidator {
    void validate(String allianceName) {
        if (isNull(allianceName)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "allianceName", "must not be null"), "AllianceName must not be null");
        }

        if (allianceName.isEmpty()) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "allianceName", "empty"), "AllianceName is empty");
        }

        if (allianceName.length() > 30) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "allianceName", "too long"), "AllianceName too long");
        }
    }
}
