package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
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

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final NotebookViewFactory notebookViewFactory;
    private final ErrorReporterService errorReporterService;

    public List<NotebookView> search(UUID userId, String searchValue) {
        if (searchValue.length() < 3) {
            throw ExceptionFactory.invalidParam("search", "too short");
        }

        return Stream.concat(searchByTitle(userId, searchValue), searchByContent(userId, searchValue))
            .distinct()
            .map(notebookViewFactory::create)
            .collect(Collectors.toList());
    }

    private Stream<ListItem> searchByContent(UUID userId, String searchValue) {
        return listItemDao.getByUserId(userId)
            .stream()
            .filter(listItem -> listItem.getTitle().toLowerCase().contains(searchValue.toLowerCase()));
    }

    private Stream<ListItem> searchByTitle(UUID userId, String searchValue) {
        return contentDao.getByUserId(userId)
            .stream()
            .filter(content -> content.getContent().toLowerCase().contains(searchValue.toLowerCase()))
            .filter(content -> !isNull(content.getListItemId()))
            .flatMap(this::fetchListItem);
    }

    private Stream<ListItem> fetchListItem(Content content) {
        Optional<ListItem> listItem = listItemDao.findById(content.getListItemId());
        if (listItem.isPresent()) {
            return Stream.of(listItem.get());
        }

        errorReporterService.report("ListItem not found with id " + content.getListItemId() + " for Content " + content.getContentId());

        return Stream.empty();
    }
}
