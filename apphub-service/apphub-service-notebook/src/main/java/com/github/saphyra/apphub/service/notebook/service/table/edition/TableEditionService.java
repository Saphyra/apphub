package com.github.saphyra.apphub.service.notebook.service.table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.edition.table_head.EditTableTableHeadService;
import com.github.saphyra.apphub.service.notebook.service.table.edition.table_join.EditTableTableJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class TableEditionService {
    private final EditTableRequestValidator editTableRequestValidator;
    private final EditTableTableHeadService editTableTableHeadService;
    private final EditTableTableJoinService editTableTableJoinService;

    private final ListItemDao listItemDao;

    @Transactional
    public ListItem edit(UUID listItemId, EditTableRequest request) {
        editTableRequestValidator.validate(request);

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        listItem.setTitle(request.getTitle());
        listItemDao.save(listItem);

        editTableTableHeadService.processEditions(request.getColumnNames(), listItem);
        editTableTableJoinService.processEditions(request.getColumns(), listItem);

        return listItem;
    }
}
