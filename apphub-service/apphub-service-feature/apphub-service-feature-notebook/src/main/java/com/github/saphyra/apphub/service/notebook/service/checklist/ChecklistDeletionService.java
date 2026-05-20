package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistDeletionService {
    private final ListItemDao listItemDao;

    public void delete(UUID userId, UUID listItemId) {
        listItemDao.deleteChecklist(userId, listItemId);
    }
}
