package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TextValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(UUID userId, CreateTextRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());
        validate(request.getContent(), "content");
    }

    public void validate(String content, String fieldName) {
        if (isNull(content)) {
            throw ExceptionFactory.invalidParam(fieldName, "must not be null");
        }
    }
}
