package com.github.saphyra.apphub.service.notebook.service.text;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class ContentValidator {
    public void validate(String content, String fieldName) {
        if (isNull(content)) {
            throw ExceptionFactory.invalidParam(fieldName, "must not be null");
        }
    }
}
