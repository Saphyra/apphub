package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
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
class TableRowEditionService {
    private final TableRowModificationService tableRowModificationService;
    private final TableRowDeletionService tableRowDeletionService;
    private final TableRowAdditionService tableRowAdditionService;

    List<TableFileUploadResponse> processTableRows(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<Content> contents) {
        log.info("Processing TableRows...");
        List<TableFileUploadResponse> fileUploads = new ArrayList<>();

        tableRowDeletionService.processTableRowDeletion(listItemId, models, tableRows, contents);
        tableRowAdditionService.processTableRowAddition(listItemId, models, tableRows, fileUploads, contents);
        tableRowModificationService.processTableRowModification(listItemId, models, tableRows, fileUploads, contents);

        log.info("{} files have to be uploaded.", fileUploads.size());

        return fileUploads;
    }
}
