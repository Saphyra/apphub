package com.github.saphyra.apphub.service.notebook.service.category;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

//TODO unit test
@Component
@RequiredArgsConstructor
@Slf4j
class NotebookViewFactory {
    private final ContentDao contentDao;

    public NotebookView create(ListItem listItem) {
        String value = extractValue(listItem);
        return NotebookView.builder()
            .id(listItem.getListItemId())
            .title(listItem.getTitle())
            .type(listItem.getType().name())
            .value(value)
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
