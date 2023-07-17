package com.github.saphyra.apphub.service.notebook.service.checklist.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
class CreateChecklistItemRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final ChecklistItemNodeRequestValidator checklistItemNodeRequestValidator;

    void validate(CreateChecklistRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
        if (isNull(request.getNodes())) {
            throw ExceptionFactory.invalidParam("nodes", "must not be null");
        }

        request.getNodes()
            .forEach(checklistItemNodeRequestValidator::validate);
    }
}
