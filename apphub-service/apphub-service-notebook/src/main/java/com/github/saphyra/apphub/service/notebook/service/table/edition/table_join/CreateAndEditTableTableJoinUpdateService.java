package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableJoinRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableJoinFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CreateAndEditTableTableJoinUpdateService {
    private final ContentDao contentDao;
    private final TableJoinDao tableJoinDao;
    private final TableJoinFactory tableJoinFactory;

    void process(List<EditTableJoinRequest> columns, ListItem listItem) {
        columns.forEach(column -> createOrUpdate(column, listItem));
    }

    private void createOrUpdate(EditTableJoinRequest column, ListItem listItem) {
        tableJoinDao.findById(column.getTableJoinId())
            .ifPresentOrElse(
                tableJoin -> update(column, tableJoin),
                () -> create(column, listItem)
            );
    }

    private void update(EditTableJoinRequest column, TableJoin tableJoin) {
        tableJoin.setColumnIndex(column.getColumnIndex());
        tableJoin.setRowIndex(column.getRowIndex());
        tableJoinDao.save(tableJoin);

        Content content = contentDao.findByParentValidated(tableJoin.getTableJoinId());
        content.setContent(column.getContent());
        contentDao.save(content);
    }

    private void create(EditTableJoinRequest column, ListItem listItem) {
        BiWrapper<TableJoin, Content> result = tableJoinFactory.create(listItem.getListItemId(), column.getContent(), column.getRowIndex(), column.getColumnIndex(), listItem.getUserId());

        tableJoinDao.save(result.getEntity1());
        contentDao.save(result.getEntity2());
    }
}
