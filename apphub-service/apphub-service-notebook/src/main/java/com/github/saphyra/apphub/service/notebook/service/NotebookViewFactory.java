package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotebookViewFactory {
    private final ContentDao contentDao;
    private final ListItemDao listItemDao;

    public NotebookView create(ListItem listItem) {
        String value = extractValue(listItem);

        UUID parentId = listItem.getParent();
        String parentTitle = Optional.ofNullable(parentId)
            .map(listItemDao::findByIdValidated)
            .map(ListItem::getTitle)
            .orElse(null);


        return NotebookView.builder()
            .id(listItem.getListItemId())
            .title(listItem.getTitle())
            .type(listItem.getType().name())
            .value(value)
            .pinned(listItem.isPinned())
            .parentId(parentId)
            .parentTitle(parentTitle)
            .build();
    }

    private String extractValue(ListItem listItem) {
        return Optional.of(listItem)
            .filter(l -> l.getType() == ListItemType.LINK)
            .map(ListItem::getListItemId)
            .map(contentDao::findByParentValidated)
            .map(Content::getContent)
            .orElse(null);
    }
}
