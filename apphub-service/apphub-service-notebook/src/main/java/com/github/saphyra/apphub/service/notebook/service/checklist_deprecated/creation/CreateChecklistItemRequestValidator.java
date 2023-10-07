package com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistRequestDeprecated;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemNodeRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
class CreateChecklistItemRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final ChecklistItemNodeRequestValidator checklistItemNodeRequestValidator;

    void validate(CreateChecklistRequestDeprecated request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
        if (isNull(request.getNodes())) {
            throw ExceptionFactory.invalidParam("nodes", "must not be null");
        }

        request.getNodes()
            .forEach(checklistItemNodeRequestValidator::validate);
    }
}
