package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
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
class TableColumnDeletionService {
    private final ColumnDataServiceProvider columnDataServiceProvider;

    boolean processTableColumnDeletion(List<TableColumnModel> models, List<TableColumn> columns, List<Content> contents) {
        log.info("Processing TableColumn deletion... Original size: {}", columns.size());
        List<UUID> toKeepIds = models.stream()
            .map(TableColumnModel::getColumnId)
            .filter(Objects::nonNull)
            .toList();

        List<TableColumn> toDelete = columns.stream()
            .filter(tableColumn -> !toKeepIds.contains(tableColumn.getColumnId()))
            .toList();
        log.info("{} TableColumns are deleted.", toDelete.size());

        toDelete.forEach(tableColumn -> {
            getContent(contents, tableColumn.getColumnId())
                .ifPresent(content -> {
                    columnDataServiceProvider.getForType(tableColumn.getType())
                        .deleteData(content.get(tableColumn.getColumnId()));

                    content.remove(tableColumn.getColumnId());
                });

            columns.remove(tableColumn);
        });

        return !toDelete.isEmpty();
    }

    private static Optional<Content> getContent(List<Content> contents, UUID key) {
        return contents.stream()
            .filter(c -> c.getContent().containsKey(key))
            .findAny();
    }
}
