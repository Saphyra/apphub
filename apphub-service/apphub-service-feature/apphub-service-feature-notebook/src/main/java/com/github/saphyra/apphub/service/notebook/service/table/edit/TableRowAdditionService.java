package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableRowAdditionService {
    private final TableRowFactory tableRowFactory;
    private final TableRowDao tableRowDao;
    private final TableColumnAdditionService tableColumnAdditionService;

    void processTableRowAddition(UUID listItemId, List<TableRowModel> models, List<TableRow> tableRows, List<TableFileUploadResponse> fileUploads, List<Content> contents) {
        log.info("Processing TableRowAddition. Initial size: {}", tableRows.size());
        models.stream()
            .filter(model -> model.getItemType() == ItemType.NEW)
            .map(model -> tableRowFactory.create(
                listItemId,
                model.getRowIndex(),
                model.getChecked(),
                tableColumnAdditionService.createColumns(listItemId, model.getColumns(), model.getRowIndex(), fileUploads, contents)
            ))
            .forEach(tableRow -> {
                tableRows.add(tableRow);
                tableRowDao.save(tableRow);
            });
    }
}
