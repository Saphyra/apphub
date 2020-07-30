package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.service.checklist.NodeContentWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO refactor - split
public class EditChecklistItemService {
    private final ChecklistItemFactory checklistItemFactory;

    private final ListItemDao listItemDao;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    @Transactional
    public void edit(List<ChecklistItemNodeRequest> requests, UUID listItemId) {
        Map<UUID, NodeContentWrapper> actualItems = checklistItemDao.getByParent(listItemId)
            .stream()
            .map(this::assembleNodeContentWrapper)
            .collect(Collectors.toMap(nodeContentWrapper -> nodeContentWrapper.getChecklistItem().getChecklistItemId(), Function.identity()));
        deleteItems(requests, actualItems);
        updateItems(requests, actualItems);
        saveNewItems(listItemDao.findByIdValidated(listItemId), requests);
    }

    private NodeContentWrapper assembleNodeContentWrapper(ChecklistItem checklistItem) {
        return NodeContentWrapper.builder()
            .checklistItem(checklistItem)
            .content(contentDao.findByParentValidated(checklistItem.getChecklistItemId()))
            .build();
    }

    private void deleteItems(List<ChecklistItemNodeRequest> requests, Map<UUID, NodeContentWrapper> actualItems) {
        List<UUID> newIds = requests.stream()
            .map(ChecklistItemNodeRequest::getChecklistItemId)
            .collect(Collectors.toList());
        actualItems.entrySet()
            .stream()
            .filter(nodeContentWrapper -> !newIds.contains(nodeContentWrapper.getKey()))
            .forEach(entry -> deleteItem(entry.getValue()));
    }

    private void deleteItem(NodeContentWrapper wrapper) {
        checklistItemDao.delete(wrapper.getChecklistItem());
        contentDao.delete(wrapper.getContent());
    }

    private void updateItems(List<ChecklistItemNodeRequest> requests, Map<UUID, NodeContentWrapper> actualItems) {
        requests.stream()
            .filter(checklistItemNodeRequest -> !isNull(checklistItemNodeRequest.getChecklistItemId()))
            .forEach(checklistItemNodeRequest -> updateItem(checklistItemNodeRequest, actualItems.get(checklistItemNodeRequest.getChecklistItemId())));
    }

    private void updateItem(ChecklistItemNodeRequest request, NodeContentWrapper wrapper) {
        ChecklistItem checklistItem = wrapper.getChecklistItem();
        checklistItem.setChecked(request.getChecked());
        checklistItem.setOrder(request.getOrder());

        Content content = wrapper.getContent();
        content.setContent(request.getContent());

        checklistItemDao.save(checklistItem);
        contentDao.save(content);
    }

    private void saveNewItems(ListItem listItem, List<ChecklistItemNodeRequest> requests) {
        requests.stream()
            .filter(request -> isNull(request.getChecklistItemId()))
            .map(request -> checklistItemFactory.create(listItem, request))
            .forEach(wrapper -> {
                checklistItemDao.save(wrapper.getChecklistItem());
                contentDao.save(wrapper.getContent());
            });
    }
}
