package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditTableTableHeadService {
    private final EditTableTableHeadDeletionService editTableTableHeadDeletionService;
    private final EditTableTableHeadUpdateService editTableTableHeadUpdateService;
    private final EditTableTableHeadCreationService editTableTableHeadCreationService;

    public void processEditions(List<KeyValuePair<String>> columnNames, ListItem listItem) {
        editTableTableHeadDeletionService.process(columnNames, listItem.getListItemId());
        editTableTableHeadUpdateService.process(columnNames, listItem.getListItemId());
        editTableTableHeadCreationService.process(columnNames, listItem);
    }
}
