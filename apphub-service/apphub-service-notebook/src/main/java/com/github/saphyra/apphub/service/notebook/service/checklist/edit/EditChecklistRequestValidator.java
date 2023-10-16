package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemModelValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditChecklistRequestValidator {
    private final TitleValidator titleValidator;
    private final ChecklistItemModelValidator checklistItemModelValidator;

    void validate(EditChecklistRequest request) {
        titleValidator.validate(request.getTitle());

        ValidationUtil.notNull(request.getItems(), "items");

        request.getItems()
            .forEach(checklistItemModelValidator::validate);
    }
}
