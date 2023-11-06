package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistItemCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemAdditionService {
    private final ChecklistItemCreationService checklistItemCreationService;

    public void addChecklistItem(UUID userId, UUID listItemId, AddChecklistItemRequest request) {
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.notNull(request.getIndex(), "index");

        checklistItemCreationService.create(userId, listItemId, request.getIndex(), request.getContent(), false);
    }
}
