package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TableRowCloneService {
    private final DimensionFactory dimensionFactory;
    private final CheckedItemDao checkedItemDao;
    private final CheckedItemFactory checkedItemFactory;
    private final DimensionDao dimensionDao;
    private final TableColumnCloneService tableColumnCloneService;

    void cloneRows(DeprecatedListItem original, DeprecatedListItem clone) {
        dimensionDao.getByExternalReference(original.getListItemId())
            .forEach(row -> cloneRow(clone, row));
    }

    private void cloneRow(DeprecatedListItem clone, Dimension originalRow) {
        Dimension clonedRow = dimensionFactory.create(clone.getUserId(), clone.getListItemId(), originalRow.getIndex());
        dimensionDao.save(clonedRow);

        if (clone.getType() == ListItemType.CHECKLIST_TABLE) {
            CheckedItem originalCheckedItem = checkedItemDao.findByIdValidated(originalRow.getDimensionId());
            CheckedItem clonedCheckedItem = checkedItemFactory.create(clone.getUserId(), clonedRow.getDimensionId(), originalCheckedItem.getChecked());
            checkedItemDao.save(clonedCheckedItem);
        }

        tableColumnCloneService.cloneColumns(clone, originalRow, clonedRow);
    }
}
