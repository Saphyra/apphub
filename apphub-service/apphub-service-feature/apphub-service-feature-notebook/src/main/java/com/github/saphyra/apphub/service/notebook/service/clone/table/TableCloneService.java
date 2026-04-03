package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableCloneService {
    private final TableHeadCloneService tableHeadCloneService;
    private final TableRowCloneService tableRowCloneService;

    public void cloneTable(ListItem original, ListItem clone) {
        tableHeadCloneService.cloneTableHeads(original, clone);
        tableRowCloneService.cloneRows(original, clone);
    }
}
