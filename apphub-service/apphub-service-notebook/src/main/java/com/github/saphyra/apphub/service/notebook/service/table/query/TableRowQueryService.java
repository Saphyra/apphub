package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class TableRowQueryService {
    private final DimensionDao dimensionDao;
    private final CheckedItemDao checkedItemDao;
    private final TableColumnQueryService tableColumnQueryService;

    List<TableRowModel> getRows(UUID listItemId) {
        return dimensionDao.getByExternalReference(listItemId)
            .stream()
            .map(row -> TableRowModel.builder()
                .rowId(row.getDimensionId())
                .rowIndex(row.getIndex())
                .checked(checkedItemDao.findById(row.getDimensionId()).map(CheckedItem::getChecked).orElse(null))
                .itemType(ItemType.EXISTING)
                .columns(tableColumnQueryService.getColumns(row.getDimensionId()))
                .build())
            .collect(Collectors.toList());
    }
}
