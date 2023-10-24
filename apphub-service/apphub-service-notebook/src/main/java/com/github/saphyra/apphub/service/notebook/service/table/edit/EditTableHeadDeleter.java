package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableHeadDeleter {
    private final TableHeadDao tableHeadDao;
    private final ContentDao contentDao;

    void deleteTableHeads(UUID listItemId, List<TableHeadModel> tableHeads) {
        List<UUID> toKeep = tableHeads.stream()
            .filter(tableHeadModel -> tableHeadModel.getType() == ItemType.EXISTING)
            .map(TableHeadModel::getTableHeadId)
            .toList();
        log.debug("TableHeads to keep: {}", toKeep);

        tableHeadDao.getByParent(listItemId)
            .stream()
            .filter(tableHead -> !toKeep.contains(tableHead.getTableHeadId()))
            .forEach(this::deleteTableHead);
    }

    private void deleteTableHead(TableHead tableHead) {
        log.info("Deleting TableHead with id {}", tableHead.getTableHeadId());
        contentDao.deleteByParent(tableHead.getTableHeadId());
        tableHeadDao.delete(tableHead);
    }
}
