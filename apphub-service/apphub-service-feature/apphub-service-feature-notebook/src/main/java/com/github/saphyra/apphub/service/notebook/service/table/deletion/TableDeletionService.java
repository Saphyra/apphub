package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableDeletionService {
    private final DimensionDao dimensionDao;
    private final TableRowDeletionService tableRowDeletionService;

    public void delete(DeprecatedListItem listItem) {
        dimensionDao.getByExternalReference(listItem.getListItemId())
            .forEach(tableRowDeletionService::deleteRow);
    }
}
