package com.github.saphyra.apphub.service.notebook.service.custom_table;

import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CustomTableCreatedResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.custom_table.creation.CustomTableCreationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CustomTableEditionService {
    private final CustomTableCreationService customTableCreationService;
    private final CustomTableDeletionService customTableDeletionService;
    private final ListItemDao listItemDao;

    @Transactional
    public List<CustomTableCreatedResponse> editCustomTable(UUID userId, UUID listItemId, CustomTableRequest request) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);

        if (listItem.getType() != ListItemType.CUSTOM_TABLE) {
            throw ExceptionFactory.forbiddenOperation(listItemId + " is not a CustomTable, it is " + listItem.getType());
        }

        List<CustomTableCreatedResponse> result = customTableCreationService.create(userId, request);

        customTableDeletionService.deleteWithoutFileDeletion(listItem);

        return result;
    }
}
