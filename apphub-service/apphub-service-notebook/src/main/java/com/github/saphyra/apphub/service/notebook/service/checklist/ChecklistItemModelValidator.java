package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistItemModelValidator {
    private final DimensionDao dimensionDao;

    public void validateNew(List<ChecklistItemModel> items) {
        items.forEach(this::validateContent);
    }

    public void validate(ChecklistItemModel model) {
        validateContent(model);

        ValidationUtil.notNull(model.getType(), "item.type");

        if (model.getType() == ItemType.EXISTING) {
            dimensionDao.findByIdValidated(model.getChecklistItemId());
        }
    }

    private void validateContent(ChecklistItemModel checklistItemModel) {
        ValidationUtil.notNull(checklistItemModel.getContent(), "item.content");
        ValidationUtil.notNull(checklistItemModel.getChecked(), "item.checked");
        ValidationUtil.notNull(checklistItemModel.getIndex(), "item.index");
    }
}
