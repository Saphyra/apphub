package com.github.saphyra.apphub.service.notebook.service.table.deprecated_deletion;

import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class TableRowDeletionService {
    private final DimensionDao dimensionDao;
    private final TableColumnDeletionService tableColumnDeletionService;
    private final CheckedItemDao checkedItemDao;

    public void deleteRow(Dimension row) {
        tableColumnDeletionService.deleteColumnsOfRow(row.getDimensionId());

        checkedItemDao.deleteById(row.getDimensionId());

        dimensionDao.delete(row);
    }
}
