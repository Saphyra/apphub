package com.github.saphyra.apphub.service.notebook.service.only_title;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateOnlyTitleRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    void validate(UUID userId, CreateOnlyTitleRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());
    }
}
