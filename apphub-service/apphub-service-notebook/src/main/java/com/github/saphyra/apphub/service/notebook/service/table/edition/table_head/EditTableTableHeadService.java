package com.github.saphyra.apphub.service.notebook.service.table.edition.table_head;

import com.github.saphyra.apphub.api.notebook.model.request.EditTableHeadRequest;
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
    private final CreateAndEditTableTableHeadUpdateService createAndEditTableTableHeadUpdateService;

    public void processEditions(List<EditTableHeadRequest> tableHeads, ListItem listItem) {
        editTableTableHeadDeletionService.process(tableHeads, listItem.getListItemId());
        createAndEditTableTableHeadUpdateService.process(tableHeads, listItem);
    }
}
