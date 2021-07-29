package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class PinService {
    private final ListItemDao listItemDao;
    private final NotebookViewFactory notebookViewFactory;

    public void pinListItem(UUID listItemId, Boolean pinned) {
        if (isNull(pinned)) {
            throw ExceptionFactory.invalidParam("pinned", "must not be null");
        }

        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        listItem.setPinned(pinned);

        listItemDao.save(listItem);
    }

    public List<NotebookView> getPinnedItems(UUID userId) {
        return listItemDao.getByUserId(userId)
            .stream()
            .filter(ListItem::isPinned)
            .map(notebookViewFactory::create)
            .collect(Collectors.toList());
    }
}
