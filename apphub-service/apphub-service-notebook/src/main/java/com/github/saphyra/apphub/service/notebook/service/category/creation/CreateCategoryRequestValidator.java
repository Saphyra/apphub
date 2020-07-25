package com.github.saphyra.apphub.service.notebook.service.category.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CategoryRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateCategoryRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    public void validate(CategoryRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
    }
}
