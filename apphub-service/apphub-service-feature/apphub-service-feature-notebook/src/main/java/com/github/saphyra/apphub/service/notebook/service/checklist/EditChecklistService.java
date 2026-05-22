package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EditChecklistService {
    private final ChecklistValidator checklistValidator;
    private final ChecklistQueryService checklistQueryService;
    private final ListItemDao listItemDao;
    private final ChecklistItemFactory checklistItemFactory;
    private final ContentFactory contentFactory;
    private final UuidConverter uuidConverter;
    private final CommonListItemDao commonListItemDao;

    @Transactional
    public ChecklistResponse edit(UUID userId, UUID listItemId, EditChecklistRequest request) {
        checklistValidator.validate(request);

        TriWrapper<ListItem, List<ChecklistItem>, List<Content>> checklist = commonListItemDao.findChecklistValidated(userId, listItemId);

        ListItem listItem = checklist.getEntity1();
        Map<UUID, ChecklistItem> checklistItems = checklist.getEntity2()
            .stream()
            .collect(Collectors.toMap(ChecklistItem::getChecklistItemId, item -> item));
        List<Content> contents = new ArrayList<>(checklist.getEntity3());

        String originalListItemTitle = listItem.getTitle();
        listItem.setTitle(request.getTitle());

        List<ChecklistItem> deletedChecklistItems = collectDeleted(checklistItems.values(), request.getItems(), contents);
        List<ChecklistItem> newChecklistItems = processNew(listItemId, request.getItems(), contents);
        List<ChecklistItem> modifiedItems = processModified(listItemId, checklistItems, request.getItems(), contents);

        if (!listItem.getTitle().equals(originalListItemTitle)) {
            listItemDao.save(listItem);
        }

        commonListItemDao.editChecklist(listItem, deletedChecklistItems, newChecklistItems, modifiedItems, contents);

        return checklistQueryService.getChecklistResponse(userId, listItemId);
    }

    private List<ChecklistItem> processModified(UUID listItemId, Map<UUID, ChecklistItem> checklistItems, List<ChecklistItemModel> items, List<Content> contents) {
        List<ChecklistItem> modifiedItems = new ArrayList<>();
        items.stream()
            .filter(model -> model.getType() == ItemType.EXISTING)
            .forEach(model -> {
                ChecklistItem checklistItem = checklistItems.get(model.getChecklistItemId());
                Content content = findContent(model.getChecklistItemId(), contents);

                boolean modified = false;

                if (!model.getIndex().equals(checklistItem.getIndex())) {
                    checklistItem.setIndex(model.getIndex());
                    modified = true;
                }

                if (!model.getChecked().equals(checklistItem.isChecked())) {
                    checklistItem.setChecked(model.getChecked());
                    modified = true;
                }

                if (modified) {
                    modifiedItems.add(checklistItem);
                }

                String key = uuidConverter.convertDomain(model.getChecklistItemId());
                String text = content.getContent()
                    .get(key);
                if (!text.equals(model.getContent())) {
                    content.remove(key);
                    contents.add(contentFactory.create(listItemId, model.getChecklistItemId(), model.getContent()));
                }
            });
        return modifiedItems;
    }

    private Content findContent(UUID checklistItemId, List<Content> contents) {
        String key = uuidConverter.convertDomain(checklistItemId);

        return contents.stream()
            .filter(content -> content.getContent().containsKey(key))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for checklistItemId " + checklistItemId));
    }

    private List<ChecklistItem> processNew(UUID listItemId, List<ChecklistItemModel> items, List<Content> contents) {
        return items.stream()
            .filter(model -> model.getType() == ItemType.NEW)
            .map(model -> {
                ChecklistItem checklistItem = checklistItemFactory.create(listItemId, model.getChecked(), model.getIndex());
                Content content = contentFactory.create(listItemId, checklistItem.getChecklistItemId(), model.getContent());
                contents.add(content);

                return checklistItem;
            })
            .toList();
    }

    private List<ChecklistItem> collectDeleted(Collection<ChecklistItem> checklistItems, List<ChecklistItemModel> items, List<Content> contents) {
        List<UUID> toKeep = items.stream()
            .map(ChecklistItemModel::getChecklistItemId)
            .toList();

        List<ChecklistItem> deletedItems = checklistItems.stream()
            .filter(checklistItem -> !toKeep.contains(checklistItem.getChecklistItemId()))
            .toList();

        deletedItems.stream()
            .map(ChecklistItem::getChecklistItemId)
            .forEach(listItemId -> contents.forEach(content -> content.remove(uuidConverter.convertDomain(listItemId))));

        return deletedItems;
    }
}
