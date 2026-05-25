package com.github.saphyra.apphub.service.notebook.dao.migration.migrator;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.TriWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item.CheckedItemDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContent;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.DeprecatedContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.DeprecatedTableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ListItemMigratorUtil {
    private final CheckedItemDao checkedItemDao;
    private final DeprecatedContentDao contentDao;
    private final DeprecatedTableHeadDao deprecatedTableHeadDao;
    private final DimensionDao dimensionDao;
    private final ColumnTypeDao columnTypeDao;
    private final FileDao fileDao;
    private final UuidConverter uuidConverter;

    ListItem migrate(DeprecatedListItem original) {
        return migrate(original, null);
    }

    public ListItem migrate(DeprecatedListItem original, String data) {
        return ListItem.builder()
            .listItemId(original.getListItemId())
            .userId(original.getUserId())
            .parent(original.getParent())
            .type(original.getType())
            .title(original.getTitle())
            .pinned(original.isPinned())
            .archived(original.isArchived())
            .data(data)
            .build();
    }

    public List<BiWrapper<ChecklistItem, Content>> migrateChecklist(ListItem listItem) {
        return dimensionDao.getByExternalReference(listItem.getListItemId())
            .stream()
            .map((Dimension dimension) -> migrate(listItem, dimension))
            .toList();
    }

    private BiWrapper<ChecklistItem, Content> migrate(ListItem listItem, Dimension dimension) {
        log.info("Migrating ChecklistItem {}", dimension.getDimensionId());
        CheckedItem checkedItem = checkedItemDao.findByIdValidated(dimension.getDimensionId());
        DeprecatedContent content = contentDao.findByParentValidated(dimension.getDimensionId());

        return new BiWrapper<>(
            ChecklistItem.builder()
                .checklistItemId(dimension.getDimensionId())
                .listItemId(listItem.getListItemId())
                .checked(checkedItem.getChecked())
                .index(dimension.getIndex())
                .build(),
            Content.builder()
                .listItemId(listItem.getListItemId())
                .build()
                .add(dimension.getDimensionId(), content.getContent())
        );
    }

    public TriWrapper<List<TableHead>, List<TableRow>, List<Content>> migrateTable(ListItem listItem) {
        List<Content> contents = new ArrayList<>();

        return new TriWrapper<>(
            migrateTableHeads(listItem.getListItemId(), contents),
            migrateTableRows(listItem.getListItemId(), contents),
            contents
        );
    }

    private List<TableRow> migrateTableRows(UUID listItemId, List<Content> contents) {
        return dimensionDao.getByExternalReference(listItemId)
            .stream()
            .map(row -> {
                log.info("Migrating TableRow {}", row.getDimensionId());

                return TableRow.builder()
                    .listItemId(listItemId)
                    .tableRowId(row.getDimensionId())
                    .index(row.getIndex())
                    .checked(checkedItemDao.findById(row.getDimensionId()).map(CheckedItem::getChecked).orElse(null))
                    .columns(migrateColumns(listItemId, row.getDimensionId(), contents))
                    .build();
            })
            .toList();
    }

    private List<TableColumn> migrateColumns(UUID listItemId, UUID rowId, List<Content> contents) {
        return dimensionDao.getByExternalReference(rowId)
            .stream()
            .map(column -> {
                ColumnType columnType = columnTypeDao.findByIdValidated(column.getDimensionId()).getType();
                log.info("Migrating TableColumn {} with type {}", column.getDimensionId(), columnType);
                if (columnType != ColumnType.EMPTY) {
                    Content content;
                    if (columnType.isFile()) {
                        content = Content.builder()
                            .listItemId(listItemId)
                            .build()
                            .add(column.getDimensionId(), uuidConverter.convertDomain(fileDao.findByParentValidated(column.getDimensionId()).getStoredFileId()));

                    } else {
                        content = Content.builder()
                            .listItemId(listItemId)
                            .build()
                            .add(column.getDimensionId(), contentDao.findByParentValidated(column.getDimensionId()).getContent());

                    }
                    contents.add(content);
                }

                return TableColumn.builder()
                    .columnId(column.getDimensionId())
                    .index(column.getIndex())
                    .type(columnType)
                    .build();
            })
            .toList();
    }

    private List<TableHead> migrateTableHeads(UUID listItemId, List<Content> contents) {
        return deprecatedTableHeadDao.getByParent(listItemId)
            .stream()
            .map(deprecatedTableHead -> {
                DeprecatedContent deprecatedContent = contentDao.findByParentValidated(deprecatedTableHead.getTableHeadId());
                log.info("Migrating TableHead {}", deprecatedTableHead.getTableHeadId());
                Content content = Content.builder()
                    .listItemId(listItemId)
                    .build()
                    .add(deprecatedTableHead.getTableHeadId(), deprecatedContent.getContent());
                contents.add(content);

                return TableHead.builder()
                    .tableHeadId(deprecatedTableHead.getTableHeadId())
                    .index(deprecatedTableHead.getColumnIndex())
                    .build();
            })
            .toList();
    }
}
