package com.github.saphyra.apphub.service.notebook.service.checklist.create;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistItemCreationService {
    private final DimensionDao dimensionDao;
    private final DimensionFactory dimensionFactory;

    private final CheckedItemDao checkedItemDao;
    private final CheckedItemFactory checkedItemFactory;

    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    void create(UUID userId, UUID listItemId, List<ChecklistItemModel> items) {
        items.forEach(item -> create(userId, listItemId, item));
    }

    public void create(UUID userId, UUID listItemId, ChecklistItemModel item) {
        Dimension dimension = dimensionFactory.create(userId, listItemId, item.getIndex());
        dimensionDao.save(dimension);

        CheckedItem checkedItem = checkedItemFactory.create(userId, dimension.getDimensionId(), item.getChecked());
        checkedItemDao.save(checkedItem);

        Content content = contentFactory.create(listItemId, dimension.getDimensionId(), userId, item.getContent());
        contentDao.save(content);
    }
}
