package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.request.EditListItemRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.TextValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class ListItemEditionService {
    private final TextValidator textValidator;
    private final ListItemDao listItemDao;
    private final ListItemRequestValidator listItemRequestValidator;

    @Transactional
    public void edit(UUID userId, UUID listItemId, EditListItemRequest request) {
        listItemRequestValidator.validate(userId, request.getTitle(), request.getParent());

        ListItem listItem = listItemDao.findByIdValidated(userId, listItemId);
        if (listItem.getType() == ListItemType.LINK) {
            textValidator.validate(request.getValue(), "value");
            listItem.setData(request.getValue());
        }
        listItem.setTitle(request.getTitle());
        moveListItem(listItem, request.getParent());
        listItemDao.saveListItem(listItem);
    }

    public void moveListItem(UUID userId, UUID listItemId, UUID parent) {
        moveListItem(listItemDao.findByIdValidated(userId, listItemId), parent);
    }

    private void moveListItem(ListItem listItem, UUID parent) {
        validateNotOwnChild(listItem.getListItemId(), parent, listItem.getUserId());

        listItem.setParent(parent);

        listItemDao.saveListItem(listItem);
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
