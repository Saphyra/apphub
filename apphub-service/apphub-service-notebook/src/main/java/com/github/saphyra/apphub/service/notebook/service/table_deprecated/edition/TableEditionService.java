package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_head.EditTableTableHeadService;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_join.EditTableTableJoinService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
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

        editTableTableHeadService.processEditions(request.getTableHeads(), listItem);
        editTableTableJoinService.processEditions(request.getColumns(), listItem);

        return listItem;
    }
}
