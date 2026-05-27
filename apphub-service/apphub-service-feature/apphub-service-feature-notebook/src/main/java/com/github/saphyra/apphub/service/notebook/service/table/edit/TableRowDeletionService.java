package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableRowDeletionService {
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final TableRowDao tableRowDao;

    void processTableRowDeletion(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<Content> contents) {
        log.info("Processing Table Row Deletion. Original size: {}", tableRows.size());
        List<UUID> toKeepIds = models.stream()
            .map(TableRowModel::getRowId)
            .filter(Objects::nonNull)
            .toList();
        List<TableRow> deleted = tableRows.stream()
            .filter(tableRow -> !toKeepIds.contains(tableRow.getTableRowId()))
            .toList();
        log.info("{} TableRows are deleted.", deleted.size());

        deleted.forEach(tableRow -> {
            tableRow.getColumns()
                .forEach(tableColumn -> {
                    Optional<Content> maybeContent = getContent(contents, tableColumn.getColumnId());
                    maybeContent.ifPresent(content -> {
                        columnDataServiceProvider.getForType(tableColumn.getType())
                            .deleteData(content.get(tableColumn.getColumnId()));
                        content.remove(tableColumn.getColumnId());
                    });
                });

            tableRows.remove(tableRow);
            tableRowDao.delete(listItemId, tableRow.getTableRowId());
        });
    }

    private static Optional<Content> getContent(List<Content> contents, UUID key) {
        return contents.stream()
            .filter(c -> c.getContent().containsKey(key))
            .findAny();
    }
}
