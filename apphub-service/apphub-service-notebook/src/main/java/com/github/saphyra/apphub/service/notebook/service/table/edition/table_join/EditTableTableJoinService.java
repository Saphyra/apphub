package com.github.saphyra.apphub.service.notebook.service.table.edition.table_join;

import com.github.saphyra.apphub.lib.common_domain.KeyValuePair;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditTableTableJoinService {
    private final EditTableTableJoinDeletionService editTableTableJoinDeletionService;
    private final EditTableTableJoinUpdateService editTableTableJoinUpdateService;
    private final EditTableTableJoinCreationService editTableTableJoinCreationService;

    public void processEditions(List<List<KeyValuePair<String>>> columns, ListItem listItem) {
        editTableTableJoinDeletionService.process(columns, listItem.getListItemId());
        editTableTableJoinUpdateService.process(columns, listItem.getListItemId());
        editTableTableJoinCreationService.process(columns, listItem);
    }
}
