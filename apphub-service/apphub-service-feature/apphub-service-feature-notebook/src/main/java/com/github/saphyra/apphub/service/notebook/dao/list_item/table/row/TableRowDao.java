package com.github.saphyra.apphub.service.notebook.dao.list_item.table.row;

import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableRowDao {
    private final UuidConverter uuidConverter;
    private final TableRowRepository repository;
    private final TableRowConverter converter;

    public void delete(UUID listItemId, UUID tableRowId) {
        repository.delete(uuidConverter.convertDomain(listItemId), uuidConverter.convertDomain(tableRowId));
    }

    public void save(TableRow tableRow) {
        repository.save(converter.convertDomain(tableRow));
    }

    public void delete(UUID listItemId, List<TableRow> tableRows) {
        String listItemIdString = uuidConverter.convertDomain(listItemId);

        Lists.partition(tableRows, Constants.DYNAMO_DB_DELETE_MAX_BATCH_SIZE)
            .forEach(rows -> repository.delete(
                listItemIdString,
                rows.stream().map(TableRow::getTableRowId).map(uuidConverter::convertDomain).toList()
            ));
    }

    public void save(List<TableRow> rows) {
        Lists.partition(rows, Constants.DYNAMO_DB_INSERT_MAX_BATCH_SIZE)
            .forEach(tableRows -> repository.save(
                converter.convertDomain(tableRows)
            ));
    }

    public TableRow findByIdValidated(UUID listItemId, UUID rowId) {
        return converter.convertEntity(repository.findById(uuidConverter.convertDomain(listItemId), uuidConverter.convertDomain(rowId)
            ))
            .orElseThrow(() -> ExceptionFactory.notFound("Row not found by id " + rowId + " for table " + listItemId));
    }

    public List<TableRow> getByListItemId(UUID listItemId) {
        return converter.convertEntity(repository.getByListItemId(uuidConverter.convertDomain(listItemId)));
    }
}
