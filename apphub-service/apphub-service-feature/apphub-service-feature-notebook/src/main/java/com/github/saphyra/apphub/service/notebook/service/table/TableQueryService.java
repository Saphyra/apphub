package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_domain.QuadWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableQueryService {
    private final UuidConverter uuidConverter;
    private final ColumnDataServiceProvider columnDataServiceProvider;
    private final CommonListItemDao commonListItemDao;

    public TableResponse getTable(UUID userId, UUID listItemId) {
        QuadWrapper<ListItem, List<TableHead>, List<TableRow>, List<Content>> table = commonListItemDao.findTableValidated(userId, listItemId);
        ListItem listItem = table.getEntity1();
        List<TableHead> tableHeads = table.getEntity2();
        List<TableRow> tableRows = table.getEntity3();
        List<Content> contents = table.getEntity4();

        return TableResponse.builder()
            .title(listItem.getTitle())
            .parent(listItem.getParent())
            .tableHeads(mapTableHeads(tableHeads, contents))
            .rows(mapRows(tableRows, contents))
            .build();
    }

    private List<TableRowModel> mapRows(List<TableRow> tableRows, List<Content> contents) {
        return tableRows.stream()
            .map(tableRow -> TableRowModel.builder()
                .rowId(tableRow.getTableRowId())
                .rowIndex(tableRow.getIndex())
                .checked(tableRow.getChecked())
                .columns(mapColumns(tableRow.getColumns(), contents))
                .itemType(ItemType.EXISTING)
                .build())
            .toList();
    }

    private List<TableColumnModel> mapColumns(List<TableColumn> columns, List<Content> contents) {
        return columns.stream()
            .map(tableColumn -> TableColumnModel.builder()
                .columnId(tableColumn.getColumnId())
                .columnIndex(tableColumn.getIndex())
                .columnType(tableColumn.getType())
                .data(
                    getContent(tableColumn.getColumnId(), contents)
                        .map(data -> columnDataServiceProvider.getForType(tableColumn.getType()).deserialize(data))
                        .orElse(null)
                )
                .itemType(ItemType.EXISTING)
                .build())
            .toList();
    }

    private List<TableHeadModel> mapTableHeads(List<TableHead> tableHeads, List<Content> contents) {
        return tableHeads.stream()
            .map(tableHead -> TableHeadModel.builder()
                .tableHeadId(tableHead.getTableHeadId())
                .columnIndex(tableHead.getIndex())
                .content(getContent(tableHead.getTableHeadId(), contents).orElseThrow(() -> ExceptionFactory.notFound("Content not found by id " + tableHead.getTableHeadId())))
                .type(ItemType.EXISTING)
                .build())
            .toList();
    }

    private Optional<String> getContent(UUID id, List<Content> contents) {
        String key = uuidConverter.convertDomain(id);

        return contents.stream()
            .flatMap(content -> content.getContent().entrySet().stream())
            .filter(entry -> entry.getKey().equals(key))
            .findFirst()
            .map(Map.Entry::getValue);
    }
}
