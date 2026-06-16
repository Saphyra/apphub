package com.github.saphyra.apphub.service.notebook.service.validator;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.common.NotebookConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(UUID userId, CreateTextRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());
        validate(request.getContent(), "content");
    }

    public void validate(String content, String fieldName) {
        ValidationUtil.notNull(content, fieldName);
        ValidationUtil.maxLength(content, NotebookConstants.MAX_CONTENT_LENGTH, fieldName);
    }
}
