package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChecklistItemFactory {
    private final ContentFactory contentFactory;
    private final IdGenerator idGenerator;

    public NodeContentWrapper create(ListItem listItem, ChecklistItemNodeRequest nodeRequest) {
        UUID checklistItemId = idGenerator.randomUUID();
        ChecklistItem checklistItem = ChecklistItem.builder()
            .checklistItemId(checklistItemId)
            .userId(listItem.getUserId())
            .parent(listItem.getListItemId())
            .order(nodeRequest.getOrder())
            .checked(nodeRequest.getChecked())
            .build();
        Content content = contentFactory.create(checklistItemId, listItem.getUserId(), nodeRequest.getContent());

        return NodeContentWrapper.builder()
            .checklistItem(checklistItem)
            .content(content)
            .build();
    }
}
