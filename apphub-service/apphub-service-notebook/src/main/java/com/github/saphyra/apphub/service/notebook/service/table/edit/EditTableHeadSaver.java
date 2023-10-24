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

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableHeadSaver {
    private final TableHeadDao tableHeadDao;
    private final TableHeadFactory tableHeadFactory;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;

    void saveTableHeads(ListItem listItem, List<TableHeadModel> tableHeads) {
        tableHeads.forEach(tableHeadModel -> saveTableHead(listItem, tableHeadModel));
    }

    private void saveTableHead(ListItem listItem, TableHeadModel tableHeadModel) {
        if (tableHeadModel.getType() == ItemType.EXISTING) {
            handleExisting(tableHeadModel);
        } else {
            handleNew(listItem, tableHeadModel);
        }
    }

    private void handleExisting(TableHeadModel tableHeadModel) {
        TableHead tableHead = tableHeadDao.findByIdValidated(tableHeadModel.getTableHeadId());
        tableHead.setColumnIndex(tableHeadModel.getColumnIndex());
        tableHeadDao.save(tableHead);

        Content content = contentDao.findByParentValidated(tableHeadModel.getTableHeadId());
        content.setContent(tableHeadModel.getContent());
        contentDao.save(content);
    }

    private void handleNew(ListItem listItem, TableHeadModel tableHeadModel) {
        TableHead tableHead = tableHeadFactory.create(listItem.getUserId(), listItem.getListItemId(), tableHeadModel.getColumnIndex());
        tableHeadDao.save(tableHead);

        Content content = contentFactory.create(listItem.getListItemId(), tableHead.getTableHeadId(), listItem.getUserId(), tableHeadModel.getContent());
        contentDao.save(content);
    }
}
