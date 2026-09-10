package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.feature.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.FutureWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final ExecutorServiceBean executorServiceBean;
    private final AccessTokenProvider accessTokenProvider;

    public List<NotebookView> search(UUID userId, String searchValue) {
        if (searchValue.length() < 3) {
            throw ExceptionFactory.invalidParam("search", "too short");
        }

        String searchValueLower = searchValue.toLowerCase();

        List<FutureWrapper<BiWrapper<ListItem, Boolean>>> futures = listItemDao.getByUserId(userId)
            .stream()
            .map(listItem -> executorServiceBean.asyncProcess(() -> new BiWrapper<>(listItem, isMatching(userId, searchValueLower, listItem))))
            .toList();

        List<ListItem> listItems = futures.stream()
            .map(FutureWrapper::get)
            .map(ExecutionResult::getOrThrow)
            .filter(BiWrapper::getEntity2)
            .map(BiWrapper::getEntity1)
            .toList();
        return notebookViewFactory.create(listItems);
    }

    @SneakyThrows
    private boolean isMatching(UUID userId, String searchValue, ListItem listItem) {
        try (var _ = accessTokenProvider.set(AccessToken.builder().userId(userId).build())) {
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
}
