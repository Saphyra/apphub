package com.github.saphyra.apphub.service.notebook.service.checklist.create;

import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemModelValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CreateChecklistRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final ChecklistItemModelValidator checklistItemModelValidator;

    void validate(CreateChecklistRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
        checklistItemModelValidator.validateNew(request.getItems());
    }
}
