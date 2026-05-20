package com.github.saphyra.apphub.service.notebook.service.table.deprecated_edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
class EditTableHeadService {
    private final EditTableHeadSaver editTableHeadSaver;
    private final EditTableHeadDeleter editTableHeadDeleter;

    void editTableHeads(DeprecatedListItem listItem, List<TableHeadModel> tableHeads) {
        editTableHeadDeleter.deleteTableHeads(listItem.getListItemId(), tableHeads);
        editTableHeadSaver.saveTableHeads(listItem, tableHeads);
    }
}
