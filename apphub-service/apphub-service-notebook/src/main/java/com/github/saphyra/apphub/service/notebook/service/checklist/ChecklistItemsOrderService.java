package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemsOrderService {
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    public void orderChecklistItems(UUID listItemId) {
        List<ChecklistItem> items = checklistItemDao.getByParent(listItemId)
            .stream()
            .map(checklistItem -> new BiWrapper<>(checklistItem, contentDao.findByParentValidated(checklistItem.getChecklistItemId())))
            .sorted(Comparator.comparing(o -> o.getEntity2().getContent()))
            .map(BiWrapper::getEntity1)
            .collect(Collectors.toList());

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOrder(i);
        }

        checklistItemDao.saveAll(items);
    }
}
