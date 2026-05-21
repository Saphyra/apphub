package com.github.saphyra.apphub.service.notebook.dao.list_item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableRowDao {
    public void delete(UUID userId, UUID listItemId, UUID tableRowId) {
    }

    public void save(TableRow tableRow) {

    }

    public void delete(UUID userId, UUID listItemId, List<TableRow> tableRows) {

    }

    public void save(UUID userId, UUID listItemId, List<TableRow> clonedTableRows) {

    }

    public TableRow findByIdValidated(UUID userId, UUID listItemId, UUID rowId) {
        return null;
    }
}
