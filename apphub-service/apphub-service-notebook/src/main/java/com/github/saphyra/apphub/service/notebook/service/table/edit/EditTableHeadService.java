package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.ItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditTableHeadService {
    private final TableHeadDao tableHeadDao;
    private final TableHeadFactory tableHeadFactory;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    void editTableHeads(ListItem listItem, List<TableHeadModel> tableHeads) {
        deleteTableHeads(listItem.getListItemId(), tableHeads);
        saveTableHeads(listItem, tableHeads);
    }

    private void deleteTableHeads(UUID listItemId, List<TableHeadModel> tableHeads) {
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

    private void saveTableHeads(ListItem listItem, List<TableHeadModel> tableHeads) {
        tableHeads.forEach(tableHeadModel -> saveTableHead(listItem, tableHeadModel));
    }

    private void saveTableHead(ListItem listItem, TableHeadModel tableHeadModel) {
        TableHead tableHead = getTableHead(listItem, tableHeadModel);
        log.debug("TableHead to save: {}", tableHead);
        tableHeadDao.save(tableHead);

        Content content = getContent(listItem, tableHeadModel.getType(), tableHead.getTableHeadId(), tableHeadModel.getContent());
        log.debug("Content to save: {}", content);
        contentDao.save(content);
    }

    private TableHead getTableHead(ListItem listItem, TableHeadModel tableHeadModel) {
        if (tableHeadModel.getType() == ItemType.EXISTING) {
            TableHead tableHead = tableHeadDao.findByIdValidated(tableHeadModel.getTableHeadId());
            tableHead.setColumnIndex(tableHeadModel.getColumnIndex());
            return tableHead;
        } else {
            return tableHeadFactory.create(listItem.getUserId(), listItem.getListItemId(), tableHeadModel.getColumnIndex());
        }
    }

    private Content getContent(ListItem listItem, ItemType itemType, UUID parent, String contentValue) {
        if (itemType == ItemType.EXISTING) {
            Content content = contentDao.findByParentValidated(parent);
            content.setContent(contentValue);
            return content;
        } else {
            return contentFactory.create(listItem.getListItemId(), parent, listItem.getUserId(), contentValue);
        }
    }
}
