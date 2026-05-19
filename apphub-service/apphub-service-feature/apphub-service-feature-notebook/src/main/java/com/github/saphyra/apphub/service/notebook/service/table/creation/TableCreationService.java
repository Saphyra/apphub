package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.table.validator.TableCreationRequestValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableCreationService {
    private final TableCreationRequestValidator tableCreationRequestValidator;
    private final DeprecatedListItemFactory listItemFactory;
    private final DeprecatedListItemDao listItemDao;
    private final TableRowCreationService tableRowCreationService;
    private final TableHeadCreationService tableHeadCreationService;

    @Transactional
    public List<TableFileUploadResponse> create(UUID userId, CreateTableRequest request) {
        tableCreationRequestValidator.validate(request);

        DeprecatedListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), request.getListItemType());
        listItemDao.save(listItem);

        tableHeadCreationService.saveTableHeads(userId, request, listItem);

        return tableRowCreationService.saveRows(userId, listItem.getListItemId(), request.getRows(), request.getListItemType());
    }
}
