package com.github.saphyra.apphub.service.notebook.service.checklist.query;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistResponseFactory {
    private final ChecklistItemModelQueryService checklistItemModelQueryService;

    public ChecklistResponse create(ListItem listItem) {
        return ChecklistResponse.builder()
            .title(listItem.getTitle())
            .parent(listItem.getParent())
            .items(checklistItemModelQueryService.getItems(listItem.getListItemId()))
            .build();
    }
}
