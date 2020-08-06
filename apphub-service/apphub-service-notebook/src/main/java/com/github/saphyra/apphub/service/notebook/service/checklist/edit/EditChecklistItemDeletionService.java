package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EditChecklistItemDeletionService {
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    void deleteItems(List<ChecklistItemNodeRequest> requests, Map<UUID, BiWrapper<ChecklistItem, Content>> actualItems) {
        List<UUID> newIds = requests.stream()
            .filter(request -> !isNull(request.getChecklistItemId()))
            .map(ChecklistItemNodeRequest::getChecklistItemId)
            .collect(Collectors.toList());
        actualItems.entrySet()
            .stream()
            .filter(nodeContentWrapper -> !newIds.contains(nodeContentWrapper.getKey()))
            .forEach(entry -> deleteItem(entry.getValue()));
    }

    private void deleteItem(BiWrapper<ChecklistItem, Content> wrapper) {
        checklistItemDao.delete(wrapper.getEntity1());
        contentDao.delete(wrapper.getEntity2());
    }
}
