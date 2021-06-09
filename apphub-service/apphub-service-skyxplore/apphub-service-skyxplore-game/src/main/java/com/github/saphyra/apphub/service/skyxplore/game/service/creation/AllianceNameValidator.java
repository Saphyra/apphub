package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class AllianceNameValidator {
    void validate(String allianceName) {
        if (isNull(allianceName)) {
            throw ExceptionFactory.invalidParam("allianceName", "must not be null");
        }

        if (allianceName.isEmpty()) {
            throw ExceptionFactory.invalidParam("allianceName", "empty");
        }

        if (allianceName.length() > 30) {
            throw ExceptionFactory.invalidParam("allianceName", "too long");
        }
    }
}
