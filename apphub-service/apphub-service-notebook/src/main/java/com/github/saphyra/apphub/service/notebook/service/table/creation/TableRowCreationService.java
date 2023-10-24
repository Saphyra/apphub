package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
class TableRowCreationService {
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final CheckedItemFactory checkedItemFactory;
    private final CheckedItemDao checkedItemDao;
    private final TableColumnCreationService tableColumnCreationService;

    List<TableFileUploadResponse> saveRows(UUID userId, UUID listItemId, List<TableRowModel> rows, ListItemType listItemType) {
        return rows.stream()
            .flatMap(tableRowModel -> saveRow(userId, listItemId, tableRowModel, listItemType))
            .toList();
    }

    private Stream<TableFileUploadResponse> saveRow(UUID userId, UUID listItemId, TableRowModel model, ListItemType listItemType) {
        Dimension row = dimensionFactory.create(userId, listItemId, model.getRowIndex());
        dimensionDao.save(row);

        if (listItemType == ListItemType.CHECKLIST_TABLE) {
            CheckedItem checkedItem = checkedItemFactory.create(userId, row.getDimensionId(), model.getChecked());
            checkedItemDao.save(checkedItem);
        }

        return tableColumnCreationService.saveColumns(userId, listItemId, row, model.getColumns()).stream();
    }
}
