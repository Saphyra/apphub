package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChecklistItemFactory {
    private final ContentFactory contentFactory;
    private final IdGenerator idGenerator;

    public BiWrapper<ChecklistItem, Content> create(ListItem listItem, ChecklistItemNodeRequest nodeRequest) {
        UUID checklistItemId = idGenerator.randomUuid();
        ChecklistItem checklistItem = ChecklistItem.builder()
            .checklistItemId(checklistItemId)
            .userId(listItem.getUserId())
            .parent(listItem.getListItemId())
            .order(nodeRequest.getOrder())
            .checked(nodeRequest.getChecked())
            .build();
        Content content = contentFactory.create(listItem.getListItemId(), checklistItemId, listItem.getUserId(), nodeRequest.getContent());

        return new BiWrapper<>(checklistItem, content);
    }
}
