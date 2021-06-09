package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class TitleValidator {
    public void validate(String title) {
        if (isBlank(title)) {
            throw ExceptionFactory.invalidParam("title", "must not be null or blank");
        }
    }
}
