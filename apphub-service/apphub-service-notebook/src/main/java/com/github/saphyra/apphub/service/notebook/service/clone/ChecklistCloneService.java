package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ChecklistCloneService {
    private final DimensionDao dimensionDao;
    private final DimensionFactory dimensionFactory;
    private final CheckedItemDao checkedItemDao;
    private final CheckedItemFactory checkedItemFactory;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    void clone(ListItem original, ListItem clone) {
        dimensionDao.getByExternalReference(original.getListItemId())
            .forEach(dimension -> clone(clone, dimension));
    }

    private void clone(ListItem clone, Dimension originalItem) {
        Dimension clonedItem = dimensionFactory.create(clone.getUserId(), clone.getListItemId(), originalItem.getIndex());
        dimensionDao.save(clonedItem);

        CheckedItem originalCheckedItem = checkedItemDao.findByIdValidated(originalItem.getDimensionId());
        CheckedItem clonedCheckedItem = checkedItemFactory.create(clone.getUserId(), clonedItem.getDimensionId(), originalCheckedItem.getChecked());
        checkedItemDao.save(clonedCheckedItem);

        Content originalContent = contentDao.findByParentValidated(originalItem.getDimensionId());
        Content clonedContent = contentFactory.create(clone.getListItemId(), clonedItem.getDimensionId(), clone.getUserId(), originalContent.getContent());
        contentDao.save(clonedContent);
    }
}
