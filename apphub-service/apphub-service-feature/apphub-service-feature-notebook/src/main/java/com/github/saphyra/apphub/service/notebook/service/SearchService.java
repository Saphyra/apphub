package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final ListItemDao listItemDao;
    private final NotebookViewFactory notebookViewFactory;
    private final ContentDao contentDao;

    public List<NotebookView> search(UUID userId, String searchValue) {
        if (searchValue.length() < 3) {
            throw ExceptionFactory.invalidParam("search", "too short");
        }

        String searchValueLower = searchValue.toLowerCase();

        return listItemDao.getByUserId(userId)
            .stream()
            .filter(listItem -> isMatching(searchValueLower, listItem))
            .map(notebookViewFactory::create)
            .toList();
    }

    private boolean isMatching(String searchValue, ListItem listItem) {
        if (listItem.getTitle().toLowerCase().contains(searchValue)) {
            return true;
        }

        if (Optional.ofNullable(listItem.getData()).map(String::toLowerCase).filter(v -> v.contains(searchValue)).isPresent()) {
            return true;
        }

        return contentDao.getByListItemId(listItem.getListItemId())
            .stream()
            .flatMap(content -> content.getContent().values().stream())
            .map(String::toLowerCase)
            .anyMatch(value -> value.contains(searchValue));
    }
}
