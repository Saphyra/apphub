package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.text.ContentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListItemEditionService {
    private final ContentDao contentDao;
    private final ContentValidator contentValidator;
    private final ListItemDao listItemDao;
    private final ListItemRequestValidator listItemRequestValidator;

    @Transactional
    public void edit(UUID listItemId, EditListItemRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        if (listItem.getType() == ListItemType.LINK) {
            contentValidator.validate(request.getValue(), "value");
            Content content = contentDao.findByParentValidated(listItemId);
            content.setContent(request.getValue());
            contentDao.save(content);
        }
        listItem.setTitle(request.getTitle());
        moveListItem(listItem, request.getParent());
        listItemDao.save(listItem);
    }

    public void moveListItem(UUID listItemId, UUID parent) {
        moveListItem(listItemDao.findByIdValidated(listItemId), parent);
    }

    private void moveListItem(ListItem listItem, UUID parent) {
        validateNotOwnChild(listItem.getListItemId(), parent, listItem.getUserId());

        listItem.setParent(parent);

        listItemDao.save(listItem);
    }

    private void validateNotOwnChild(UUID listItemId, UUID newParent, UUID userId) {
        List<ListItem> children = listItemDao.getByUserIdAndParent(userId, listItemId);
        boolean childMatchesNewParent = children.stream()
            .anyMatch(listItem -> {
                UUID currentItemIs = listItem.getListItemId();
                return currentItemIs.equals(newParent);
            });
        if (listItemId.equals(newParent) || childMatchesNewParent) {
            throw ExceptionFactory.invalidParam("parent", "must not be own child");
        }
        children.forEach(listItem -> validateNotOwnChild(listItem.getListItemId(), newParent, userId));
    }
}
