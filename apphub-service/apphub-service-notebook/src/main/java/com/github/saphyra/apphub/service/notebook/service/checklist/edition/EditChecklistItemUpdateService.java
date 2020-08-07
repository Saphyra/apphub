package com.github.saphyra.apphub.service.notebook.service.checklist.edition;

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

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class EditChecklistItemUpdateService {
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    void updateItems(List<ChecklistItemNodeRequest> requests, Map<UUID, BiWrapper<ChecklistItem, Content>> actualItems) {
        requests.stream()
            .filter(checklistItemNodeRequest -> !isNull(checklistItemNodeRequest.getChecklistItemId()))
            .forEach(checklistItemNodeRequest -> updateItem(checklistItemNodeRequest, actualItems.get(checklistItemNodeRequest.getChecklistItemId())));
    }

    private void updateItem(ChecklistItemNodeRequest request, BiWrapper<ChecklistItem, Content> wrapper) {
        ChecklistItem checklistItem = wrapper.getEntity1();
        checklistItem.setChecked(request.getChecked());
        checklistItem.setOrder(request.getOrder());

        Content content = wrapper.getEntity2();
        content.setContent(request.getContent());

        checklistItemDao.save(checklistItem);
        contentDao.save(content);
    }
}
