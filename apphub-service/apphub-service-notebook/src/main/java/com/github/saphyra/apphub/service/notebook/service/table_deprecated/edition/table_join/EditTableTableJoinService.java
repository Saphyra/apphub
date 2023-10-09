package com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.table_join;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableJoinRequest;
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
    private final CreateAndEditTableTableJoinUpdateService createAndEditTableTableJoinUpdateService;

    public void processEditions(List<EditTableJoinRequest> columns, ListItem listItem) {
        editTableTableJoinDeletionService.process(columns, listItem.getListItemId());
        createAndEditTableTableJoinUpdateService.process(columns, listItem);
    }
}
