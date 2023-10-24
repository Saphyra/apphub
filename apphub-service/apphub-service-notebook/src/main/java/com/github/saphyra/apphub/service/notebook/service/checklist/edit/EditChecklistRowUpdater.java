package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class EditChecklistRowUpdater {
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final ContentDao contentDao;

    void updateExistingChecklistItem(ChecklistItemModel item) {
        Dimension dimension = dimensionDao.findByIdValidated(item.getChecklistItemId());
        dimension.setIndex(item.getIndex());
        dimensionDao.save(dimension);

        CheckedItem checkedItem = checkedItemDao.findByIdValidated(item.getChecklistItemId());
        checkedItem.setChecked(item.getChecked());
        checkedItemDao.save(checkedItem);

        Content content = contentDao.findByParentValidated(item.getChecklistItemId());
        content.setContent(item.getContent());
        contentDao.save(content);
    }
}
