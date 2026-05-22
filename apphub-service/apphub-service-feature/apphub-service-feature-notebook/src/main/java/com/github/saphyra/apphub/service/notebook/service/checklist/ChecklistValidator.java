package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistValidator {
    private final ListItemRequestValidator listItemRequestValidator;
    private final TitleValidator titleValidator;

    void validate(UUID userId, CreateChecklistRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());
        validateNew(request.getItems());
    }

    public void validateNew(List<ChecklistItemModel> items) {
        ValidationUtil.notNull(items, "items");

        items.forEach(this::validateContent);
    }

    public void validate(ChecklistItemModel model) {
        validateContent(model);

        ValidationUtil.notNull(model.getType(), "item.type");

        if (model.getType() == ItemType.EXISTING) {
            ValidationUtil.notNull(model.getChecklistItemId(), "item.checklistItemId");
        }
    }

    private void validateContent(ChecklistItemModel checklistItemModel) {
        ValidationUtil.notNull(checklistItemModel.getContent(), "item.content");
        ValidationUtil.notNull(checklistItemModel.getChecked(), "item.checked");
        ValidationUtil.notNull(checklistItemModel.getIndex(), "item.index");
    }

    void validate(EditChecklistRequest request) {
        titleValidator.validate(request.getTitle());

        ValidationUtil.notNull(request.getItems(), "items");

        request.getItems()
            .forEach(this::validate);
    }
}
