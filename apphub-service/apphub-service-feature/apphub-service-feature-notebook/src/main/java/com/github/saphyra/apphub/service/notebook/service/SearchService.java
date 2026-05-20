package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final ListItemDao listItemDao;
    private final NotebookViewFactory notebookViewFactory;

    public List<NotebookView> search(UUID userId, String searchValue) {
        if (searchValue.length() < 3) {
            throw ExceptionFactory.invalidParam("search", "too short");
        }

        String searchValueLower = searchValue.toLowerCase();

        return Stream.concat(searchByTitleAndData(userId, searchValueLower), searchByContent(userId, searchValueLower))
            .distinct()
            .map(notebookViewFactory::create)
            .collect(Collectors.toList());
    }

    private Stream<ListItem> searchByTitleAndData(UUID userId, String searchValueLower) {
        return listItemDao.getByUserId(userId)
            .stream()
            .filter(listItem -> listItem.getTitle().toLowerCase().contains(searchValueLower) || Optional.ofNullable(listItem.getData()).map(String::toLowerCase).orElse("").contains(searchValueLower));
    }

    private Stream<ListItem> searchByContent(UUID userId, String searchValueLower) {
        List<UUID> listItemIds = listItemDao.getContentsByUserId(userId)
            .stream()
            .filter(content -> hasMatchingContent(content, searchValueLower))
            .map(Content::getListItemId)
            .toList();

        return listItemDao.getByIds(userId, listItemIds)
            .stream();
    }

    private boolean hasMatchingContent(Content content, String searchValueLower) {
        return  content.getContent()
            .values()
            .stream()
            .map(String::toLowerCase)
            .anyMatch(value -> value.contains(searchValueLower));
    }
}
