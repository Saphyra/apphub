package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_head;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableHeadRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.TableHeadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
//TODO unit test
class CreateAndEditTableTableHeadUpdateService {
    private final ContentDao contentDao;
    private final TableHeadDao tableHeadDao;
    private final TableHeadFactory tableHeadFactory;

    public void process(List<EditTableHeadRequest> incomingTableHeads, ListItem listItem) {
        incomingTableHeads.forEach(editTableHeadRequest -> createOrUpdate(editTableHeadRequest, listItem));
    }

    private void createOrUpdate(EditTableHeadRequest editTableHeadRequest, ListItem listItem) {
        tableHeadDao.findById(editTableHeadRequest.getTableHeadId())
            .ifPresentOrElse(
                tableHead -> update(editTableHeadRequest, tableHead),
                () -> create(editTableHeadRequest, listItem)
            );
    }

    private void update(EditTableHeadRequest editTableHeadRequest, TableHead tableHead) {
        tableHead.setColumnIndex(editTableHeadRequest.getColumnIndex());

        Content content = contentDao.findByParentValidated(tableHead.getTableHeadId());
        content.setContent(editTableHeadRequest.getColumnName());
        contentDao.save(content);
    }

    private void create(EditTableHeadRequest editTableHeadRequest, ListItem listItem) {
        BiWrapper<TableHead, Content> result = tableHeadFactory.create(listItem.getListItemId(), editTableHeadRequest.getColumnName(), editTableHeadRequest.getColumnIndex(), listItem.getUserId());
        tableHeadDao.save(result.getEntity1());
        contentDao.save(result.getEntity2());
    }
}
