package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableColumn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableColumnEditionService {
    private final TableColumnModificationService tableColumnModificationService;
    private final TableColumnDeletionService tableColumnDeletionService;
    private final TableColumnAdditionService tableColumnAdditionService;

    boolean processTableColumnEdition(
        UUID listItemId,
        List<TableColumnModel> models,
        List<TableColumn> columns,
        List<Content> contents,
        List<TableFileUploadResponse> fileUploads,
        int rowIndex
    ) {
        log.info("Processing TableColumnModification...");
        boolean columnDeleted = tableColumnDeletionService.processTableColumnDeletion(models, columns, contents);
        boolean columnAdded = tableColumnAdditionService.processTableColumnAddition(listItemId, rowIndex, models, columns, contents, fileUploads);
        boolean columnModified = tableColumnModificationService.processTableColumnModification(listItemId, rowIndex, models, columns, contents, fileUploads);

        return columnDeleted || columnAdded || columnModified;
    }
}
