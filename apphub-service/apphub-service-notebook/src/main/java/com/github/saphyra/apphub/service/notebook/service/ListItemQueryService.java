package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.notebook.model.response.NotebookView;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListItemQueryService {
    private final ListItemDao listItemDao;
    private final NotebookViewFactory notebookViewFactory;

    public NotebookView findListItem(UUID listItemId) {
        return listItemDao.findById(listItemId)
            .map(notebookViewFactory::create)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ListItem not found by listItemId " + listItemId));
    }
}
