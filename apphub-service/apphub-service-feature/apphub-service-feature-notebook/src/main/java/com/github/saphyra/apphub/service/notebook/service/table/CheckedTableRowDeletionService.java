package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.service.table.deletion.TableRowDeletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckedTableRowDeletionService {
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final TableRowDeletionService tableRowDeletionService;

    @Transactional
    public void deleteCheckedRows(UUID listItemId) {
        dimensionDao.getByExternalReference(listItemId)
            .stream()
            .filter(dimension -> checkedItemDao.findByIdValidated(dimension.getDimensionId()).getChecked())
            .forEach(tableRowDeletionService::deleteRow);
    }
}
