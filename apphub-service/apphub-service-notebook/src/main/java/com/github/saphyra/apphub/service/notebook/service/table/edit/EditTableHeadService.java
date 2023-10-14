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

        tableHeadDao.getByParent(listItemId)
            .stream()
            .filter(tableHead -> !toKeep.contains(tableHead.getTableHeadId()))
            .forEach(tableHeadDao::delete);
    }

    private void saveTableHeads(ListItem listItem, List<TableHeadModel> tableHeads) {
        tableHeads.forEach(tableHeadModel -> saveTableHead(listItem, tableHeadModel));
    }

    private void saveTableHead(ListItem listItem, TableHeadModel tableHeadModel) {
        TableHead tableHead = getTableHead(listItem, tableHeadModel);
        tableHeadDao.save(tableHead);

        Content content = getContent(listItem, tableHeadModel);
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

    private Content getContent(ListItem listItem, TableHeadModel tableHeadModel) {
        if (tableHeadModel.getType() == ItemType.EXISTING) {
            Content content = contentDao.findByParentValidated(tableHeadModel.getTableHeadId());
            content.setContent(tableHeadModel.getContent());
            return content;
        } else {
            return contentFactory.create(listItem.getListItemId(), tableHeadModel.getTableHeadId(), listItem.getUserId(), tableHeadModel.getContent());
        }
    }
}
