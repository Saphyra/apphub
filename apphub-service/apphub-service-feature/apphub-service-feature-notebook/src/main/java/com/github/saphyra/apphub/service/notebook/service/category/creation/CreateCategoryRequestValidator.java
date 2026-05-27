package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.feature.notebook.model.request.CreateCategoryRequest;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateCategoryRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(UUID userId, CreateCategoryRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());
    }
}
