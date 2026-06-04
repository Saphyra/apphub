package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableRowModificationService {
    private final TableRowDao tableRowDao;
    private final TableColumnEditionService tableColumnEditionService;

    /**
     * Iterates through the existing {@link TableRowModel}s, comparing them with the existing data.
     * <p>
     * If mismatch detected, the corresponding {@link TableRow} is updated and saved.
     */
    void processTableRowModification(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<TableFileUploadResponse> fileUploads, List<Content> contents) {
        log.info("Processing TableRowModification...");
        List<TableRowModel> existing = models.stream()
            .filter(model -> model.getItemType() == ItemType.EXISTING)
            .toList();

        for (TableRowModel model : existing) {
            TableRow tableRow = tableRows.stream()
                .filter(tr -> tr.getTableRowId().equals(model.getRowId()))
                .findAny()
                .orElseThrow(() -> ExceptionFactory.notFound("TableRow not found by id " + model.getRowId()));

            boolean rowModified = false;
            if (tableRow.getChecked() != model.getChecked()) {
                log.info("Updating checked status of TableRow {}", tableRow.getTableRowId());
                tableRow.setChecked(model.getChecked());
                rowModified = true;
            }

            if (tableRow.getIndex() != model.getRowIndex()) {
                log.info("Updating index of TableRow {}", tableRow.getTableRowId());
                tableRow.setIndex(model.getRowIndex());
                rowModified = true;
            }

            ArrayList<TableColumn> columns = new ArrayList<>(tableRow.getColumns());
            boolean columnsModified = tableColumnEditionService.processTableColumnEdition(listItemId, model.getColumns(), columns, contents, fileUploads, tableRow.getIndex());
            tableRow.setColumns(columns);

            if (rowModified || columnsModified) {
                log.info("Saving modified TableRow {}", tableRow.getTableRowId());
                tableRowDao.save(tableRow);
            }
        }
    }
}
