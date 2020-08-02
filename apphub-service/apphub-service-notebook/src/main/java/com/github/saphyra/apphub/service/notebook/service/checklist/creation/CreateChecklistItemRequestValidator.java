package com.github.saphyra.apphub.service.notebook.service.checklist.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.service.notebook.service.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
class CreateChecklistItemRequestValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final ChecklistItemNodeRequestValidator checklistItemNodeRequestValidator;

    void validate(CreateChecklistItemRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());
        if (isNull(request.getNodes())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "nodes", "must not be null"), "Nodes must not be empty.");
        }

        request.getNodes()
            .forEach(checklistItemNodeRequestValidator::validate);
    }
}
