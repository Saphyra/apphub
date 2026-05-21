package com.github.saphyra.apphub.service.notebook.service.table.deprecated_edit;

import com.github.saphyra.apphub.api.feature.notebook.model.ItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.DeprecatedTableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class EditTableHeadSaver {
    private final TableHeadDao tableHeadDao;
    private final DeprecatedTableHeadFactory tableHeadFactory;
    private final ContentDao contentDao;
    private final DeprecatedContentFactory contentFactory;

    void saveTableHeads(DeprecatedListItem listItem, List<TableHeadModel> tableHeads) {
        tableHeads.forEach(tableHeadModel -> saveTableHead(listItem, tableHeadModel));
    }

    private void saveTableHead(DeprecatedListItem listItem, TableHeadModel tableHeadModel) {
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

    private void handleNew(DeprecatedListItem listItem, TableHeadModel tableHeadModel) {
        TableHead tableHead = tableHeadFactory.create(listItem.getUserId(), listItem.getListItemId(), tableHeadModel.getColumnIndex());
        tableHeadDao.save(tableHead);

        Content content = contentFactory.create(listItem.getListItemId(), tableHead.getTableHeadId(), listItem.getUserId(), tableHeadModel.getContent());
        contentDao.save(content);
    }
}
