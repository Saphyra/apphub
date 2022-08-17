package com.github.saphyra.apphub.service.notebook.service.only_title;

import com.github.saphyra.apphub.api.notebook.model.request.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CreateOnlyTitleRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;

    void validate(CreateOnlyTitleRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
    }
}
