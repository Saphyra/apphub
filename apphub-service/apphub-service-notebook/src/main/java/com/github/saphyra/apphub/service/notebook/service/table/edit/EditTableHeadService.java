package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableHeadService {
    private final EditTableHeadSaver editTableHeadSaver;
    private final EditTableHeadDeleter editTableHeadDeleter;

    void editTableHeads(ListItem listItem, List<TableHeadModel> tableHeads) {
        editTableHeadDeleter.deleteTableHeads(listItem.getListItemId(), tableHeads);
        editTableHeadSaver.saveTableHeads(listItem, tableHeads);
    }
}
