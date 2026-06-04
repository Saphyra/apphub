package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
class TableHeadEditionService {
    private final TableHeadDao tableHeadDao;
    private final TableHeadModificationService tableHeadModificationService;
    private final TableHeadAdditionService tableHeadAdditionService;
    private final TableHeadDeletionService tableHeadDeletionService;

    void processTableHeads(UUID listItemId, List<TableHeadModel> models, List<TableHead> tableHeads, List<Content> contents) {
        log.info("Processing TableHead modifications...");
        boolean tableHeadsDeleted = tableHeadDeletionService.processTableHeadDeletion(models, tableHeads, contents);
        boolean tableHeadsAdded = tableHeadAdditionService.processTableHeadAddition(listItemId, models, tableHeads, contents);
        boolean tableHeadsModified = tableHeadModificationService.processTableHeadModification(listItemId, models, tableHeads, contents);

        if (tableHeadsDeleted || tableHeadsAdded || tableHeadsModified) {
            log.info("Saving modified TableHeads...");
            tableHeadDao.save(listItemId, tableHeads);
        }
    }
}
