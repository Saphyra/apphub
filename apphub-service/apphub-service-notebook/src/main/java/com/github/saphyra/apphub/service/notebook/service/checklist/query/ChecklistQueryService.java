package com.github.saphyra.apphub.service.notebook.service.checklist.query;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistQueryService {
    private final ListItemDao listItemDao;
    private final ChecklistResponseFactory checklistResponseFactory;

    public ChecklistResponse getChecklistResponse(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        if (listItem.getType() != ListItemType.CHECKLIST) {
            throw ExceptionFactory.reportedException(HttpStatus.BAD_REQUEST, listItemId + " is not a " + ListItemType.CHECKLIST + ", it is a " + listItem.getType());
        }
        return checklistResponseFactory.create(listItem);
    }
}
