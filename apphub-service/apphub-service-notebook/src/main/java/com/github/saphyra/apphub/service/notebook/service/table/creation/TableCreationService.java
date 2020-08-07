package com.github.saphyra.apphub.service.notebook.service.table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.table.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.table.TableJoinFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableCreationService {
    private final ListItemFactory listItemFactory;
    private final TableCreationRequestValidator tableCreationRequestValidator;
    private final TableHeadFactory tableHeadFactory;
    private final TableJoinFactory tableJoinFactory;

    private final ContentDao contentDao;
    private final ListItemDao listItemDao;
    private final TableHeadDao tableHeadDao;
    private final TableJoinDao tableJoinDao;

    @Transactional
    public UUID create(CreateTableRequest request, UUID userId) {
        tableCreationRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.TABLE);

        List<BiWrapper<TableHead, Content>> tableHeads = tableHeadFactory.create(listItem, request.getColumnNames(), userId);
        List<BiWrapper<TableJoin, Content>> tableColumns = tableJoinFactory.create(listItem, request.getColumns(), userId);

        listItemDao.save(listItem);
        tableHeads.forEach(biWrapper -> {
            tableHeadDao.save(biWrapper.getEntity1());
            contentDao.save(biWrapper.getEntity2());
        });
        tableColumns.forEach(biWrapper -> {
            tableJoinDao.save(biWrapper.getEntity1());
            contentDao.save(biWrapper.getEntity2());
        });

        return listItem.getListItemId();
    }
}
