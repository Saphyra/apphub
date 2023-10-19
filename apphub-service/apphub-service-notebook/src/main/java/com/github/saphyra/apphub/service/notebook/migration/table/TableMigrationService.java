package com.github.saphyra.apphub.service.notebook.migration.table;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeFactory;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@ForRemoval("notebook-redesign")
public class TableMigrationService {
    private final ListItemDao listItemDao;
    private final AccessTokenProvider accessTokenProvider;
    private final TableJoinDao tableJoinDao;
    private final DimensionFactory dimensionFactory;
    private final DimensionDao dimensionDao;
    private final CheckedItemFactory checkedItemFactory;
    private final CheckedItemDao checkedItemDao;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final ColumnTypeFactory columnTypeFactory;
    private final ColumnTypeDao columnTypeDao;

    @Transactional
    public void migrate() {
        listItemDao.getAllUnencrypted()
            .stream()
            .filter(unencryptedListItem -> unencryptedListItem.getType() == ListItemType.TABLE || unencryptedListItem.getType() == ListItemType.CHECKLIST_TABLE)
            .forEach(this::migrate);
    }

    private void migrate(UnencryptedListItem unencryptedListItem) {
        try {
            accessTokenProvider.set(AccessTokenHeader.builder().userId(unencryptedListItem.getUserId()).build());
            doMigrate(unencryptedListItem);
        } finally {
            accessTokenProvider.clear();
        }
    }

    private void doMigrate(UnencryptedListItem unencryptedListItem) {
        tableJoinDao.getByParent(unencryptedListItem.getListItemId())
            .stream()
            .collect(Collectors.groupingBy(TableJoin::getRowIndex))
            .forEach((rowIndex, columns) -> migrateRow(unencryptedListItem, rowIndex, columns));
    }

    private void migrateRow(UnencryptedListItem listItem, Integer rowIndex, List<TableJoin> columns) {
        Dimension row = dimensionFactory.create(listItem.getUserId(), listItem.getListItemId(), rowIndex);
        dimensionDao.save(row);

        if (listItem.getType() == ListItemType.CHECKLIST_TABLE) {
            boolean checked = checklistTableRowDao.getByParent(listItem.getListItemId())
                .stream()
                .filter(checklistRow -> checklistRow.getRowIndex().equals(rowIndex))
                .findAny()
                .map(ChecklistTableRow::isChecked)
                .orElse(false);

            CheckedItem checkedItem = checkedItemFactory.create(listItem.getUserId(), row.getDimensionId(), checked);
            checkedItemDao.save(checkedItem);
        }

        columns.forEach(column -> migrateColumn(listItem, row.getDimensionId(), column));
    }

    private void migrateColumn(UnencryptedListItem listItem, UUID rowId, TableJoin originalColumn) {
        Dimension column = dimensionFactory.create(listItem.getUserId(), rowId, originalColumn.getColumnIndex(), originalColumn.getTableJoinId());
        dimensionDao.save(column);

        ColumnTypeDto columnType = columnTypeFactory.create(originalColumn.getTableJoinId(), listItem.getUserId(), ColumnType.TEXT);
        columnTypeDao.save(columnType);
    }
}
