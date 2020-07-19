package com.github.saphyra.apphub.service.notebook.service.text.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTextRequest;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CreateTextRequestValidator {
    private final ContentValidator contentValidator;
    private final ListItemRequestValidator listItemRequestValidator;

    void validate(CreateTextRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
        contentValidator.validate(request.getContent());
    }
}
