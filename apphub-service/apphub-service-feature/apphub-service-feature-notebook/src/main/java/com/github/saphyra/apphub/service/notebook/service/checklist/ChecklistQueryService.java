package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistQueryService {
    private final ListItemDao listItemDao;
    private final UuidConverter uuidConverter;

    public ChecklistResponse getChecklistResponse(UUID userId, UUID listItemId) {
        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> checklist = listItemDao.findChecklistValidated(userId, listItemId);
        ListItem listItem = checklist.getEntity1();
        List<ChecklistItem> checklistItems = checklist.getEntity2();
        List<Content> contents = checklist.getEntity3();

        return ChecklistResponse.builder()
            .title(listItem.getTitle())
            .parent(listItem.getParent())
            .items(mapItems(checklistItems, mapContents(contents)))
            .build();
    }

    private Map<UUID, String> mapContents(List<Content> contents) {
        return contents.stream()
            .flatMap(content -> content.getContent().entrySet().stream())
            .collect(Collectors.toMap(entry -> uuidConverter.convertEntity(entry.getKey()), Map.Entry::getValue));
    }

    private List<ChecklistItemModel> mapItems(List<ChecklistItem> checklistItems, Map<UUID, String> contents) {
        return checklistItems.stream()
            .map(checklistItem -> ChecklistItemModel.builder()
                .checklistItemId(checklistItem.getChecklistItemId())
                .index(checklistItem.getIndex())
                .checked(checklistItem.isChecked())
                .content(contents.get(checklistItem.getChecklistItemId()))
                .type(ItemType.EXISTING)
                .build())
            .toList();
    }
}
