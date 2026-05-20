package com.github.saphyra.apphub.service.notebook.service.checklist;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class OrderChecklistItemsService {
    private final ChecklistQueryService checklistQueryService;
    private final ListItemDao listItemDao;
    private final UuidConverter uuidConverter;

    public ChecklistResponse orderItems(UUID userId, UUID listItemId) {
        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> checklist = listItemDao.findChecklistValidated(userId, listItemId);
        Map<UUID, ChecklistItem> checklistItems = checklist.getEntity2()
            .stream()
            .collect(Collectors.toMap(ChecklistItem::getChecklistItemId, item -> item));
        List<Content> contents = checklist.getEntity3();

        List<UUID> checklistItemsOrdered = order(contents);
        List<ChecklistItem> modifiedItems = new ArrayList<>();
        for(int i = 0; i  < checklistItemsOrdered.size(); i++) {
            UUID checklistItemId = checklistItemsOrdered.get(i);
            ChecklistItem checklistItem = checklistItems.get(checklistItemId);
            int originalIndex = checklistItem.getIndex();
            if(i != originalIndex){
                checklistItem.setIndex(i);
                modifiedItems.add(checklistItem);
            }
        }

        listItemDao.saveChecklistItems(modifiedItems);

        return checklistQueryService.getChecklistResponse(userId, listItemId);
    }

    private List<UUID> order(List<Content> contents) {
        return contents.stream()
            .flatMap(content -> content.getContent().entrySet().stream())
            .sorted(Map.Entry.comparingByValue())
            .map(entry -> uuidConverter.convertEntity(entry.getKey()))
            .toList();
    }
}
