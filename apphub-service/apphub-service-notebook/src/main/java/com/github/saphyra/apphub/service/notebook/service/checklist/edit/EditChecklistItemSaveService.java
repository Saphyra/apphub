package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EditChecklistItemSaveService {
    private final ChecklistItemFactory checklistItemFactory;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    void saveNewItems(List<ChecklistItemNodeRequest> requests, ListItem listItem) {
        requests.stream()
            .filter(request -> isNull(request.getChecklistItemId()))
            .map(request -> checklistItemFactory.create(listItem, request))
            .forEach(wrapper -> {
                checklistItemDao.save(wrapper.getEntity1());
                contentDao.save(wrapper.getEntity2());
            });
    }
}
