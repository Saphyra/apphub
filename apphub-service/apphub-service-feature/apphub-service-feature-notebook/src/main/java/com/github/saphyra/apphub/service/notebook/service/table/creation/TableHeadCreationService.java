package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.Content;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class TableHeadCreationService {
    private final TableHeadFactory tableHeadFactory;
    private final TableHeadDao tableHeadDao;
    private final ContentFactory contentFactory;
    private final ContentDao contentDao;

     void saveTableHeads(UUID userId, CreateTableRequest request, DeprecatedListItem listItem) {
        request.getTableHeads()
            .forEach(tableHeadModel -> saveTableHead(userId, listItem.getListItemId(), tableHeadModel));
    }

    private void saveTableHead(UUID userId, UUID listItemId, TableHeadModel tableHeadModel) {
        TableHead tableHead = tableHeadFactory.create(userId, listItemId, tableHeadModel.getColumnIndex());
        tableHeadDao.save(tableHead);

        Content content = contentFactory.create(listItemId, tableHead.getTableHeadId(), userId, tableHeadModel.getContent());
        contentDao.save(content);
    }
}
