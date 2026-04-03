package com.github.saphyra.apphub.service.notebook.service.table.deletion;

import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableDeletionService {
    private final DimensionDao dimensionDao;
    private final TableRowDeletionService tableRowDeletionService;

    public void delete(ListItem listItem) {
        dimensionDao.getByExternalReference(listItem.getListItemId())
            .forEach(tableRowDeletionService::deleteRow);
    }
}
