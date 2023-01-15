package com.github.saphyra.apphub.service.notebook.service.checklist.edition;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.TitleValidator;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemNodeRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditChecklistItemService {
    private final ChecklistItemNodeRequestValidator checklistItemNodeRequestValidator;
    private final ListItemDao listItemDao;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;
    private final EditChecklistItemDeletionService editChecklistItemDeletionService;
    private final EditChecklistItemUpdateService editChecklistItemUpdateService;
    private final EditChecklistItemSaveService editChecklistItemSaveService;
    private final TitleValidator titleValidator;

    @Transactional
    public void edit(EditChecklistItemRequest request, UUID listItemId) {
        titleValidator.validate(request.getTitle());
        List<ChecklistItemNodeRequest> nodes = request.getNodes();
        nodes.forEach(checklistItemNodeRequestValidator::validate);

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        listItem.setTitle(request.getTitle());
        listItemDao.save(listItem);

        Map<UUID, BiWrapper<ChecklistItem, Content>> actualItems = checklistItemDao.getByParent(listItemId)
            .stream()
            .map(this::assembleNodeContentWrapper)
            .collect(Collectors.toMap(nodeContentWrapper -> nodeContentWrapper.getEntity1().getChecklistItemId(), Function.identity()));
        editChecklistItemDeletionService.deleteItems(nodes, actualItems);
        editChecklistItemUpdateService.updateItems(nodes, actualItems);
        editChecklistItemSaveService.saveNewItems(nodes, listItem);
    }

    private BiWrapper<ChecklistItem, Content> assembleNodeContentWrapper(ChecklistItem checklistItem) {
        return new BiWrapper<>(
            checklistItem,
            contentDao.findByParentValidated(checklistItem.getChecklistItemId())
        );
    }
}
