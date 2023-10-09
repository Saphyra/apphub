package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistDeletionService {
    private final DimensionDao dimensionDao;
    private final ChecklistItemDeletionService checklistItemDeletionService;

    public void delete(UUID listItemId) {
        dimensionDao.getByExternalReference(listItemId)
            .forEach(checklistItemDeletionService::deleteChecklistItem);
    }
}
