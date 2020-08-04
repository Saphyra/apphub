package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.response.ChecklistItemResponse;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemQueryService {
    private final ListItemDao listItemDao;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    public ChecklistResponse query(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        return ChecklistResponse.builder()
            .title(listItem.getTitle())
            .nodes(queryNodes(listItemId))
            .build();
    }

    private List<ChecklistItemResponse> queryNodes(UUID listItemId) {
        return checklistItemDao.getByParent(listItemId)
            .stream()
            .map(this::assembleResponse)
            .collect(Collectors.toList());
    }

    private ChecklistItemResponse assembleResponse(ChecklistItem checklistItem) {
        Content content = contentDao.findByParentValidated(checklistItem.getChecklistItemId());

        return ChecklistItemResponse.builder()
            .checklistItemId(checklistItem.getChecklistItemId())
            .content(content.getContent())
            .checked(checklistItem.getChecked())
            .order(checklistItem.getOrder())
            .build();
    }
}
