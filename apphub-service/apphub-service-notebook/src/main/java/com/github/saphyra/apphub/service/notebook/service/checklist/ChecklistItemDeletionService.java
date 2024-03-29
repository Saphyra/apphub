package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemDeletionService {
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final ContentDao contentDao;

    @Transactional
    public void deleteChecklistItem(UUID checklistItemId) {
        Dimension dimension = dimensionDao.findByIdValidated(checklistItemId);

        deleteChecklistItem(dimension);
    }

    @Transactional
    public void deleteChecklistItem(Dimension dimension) {
        checkedItemDao.deleteById(dimension.getDimensionId());
        contentDao.deleteByParent(dimension.getDimensionId());
        dimensionDao.delete(dimension);
    }
}
