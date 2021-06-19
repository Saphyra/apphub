package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityValidator {
    public void validate(Integer priority) {
        if (isNull(priority)) {
            throw ExceptionFactory.invalidParam("priority", "must not be null");
        }

        if (priority < 1) {
            throw ExceptionFactory.invalidParam("priority", "too low");
        }

        if (priority > 10) {
            throw ExceptionFactory.invalidParam("priority", "too high");
        }
    }
}
