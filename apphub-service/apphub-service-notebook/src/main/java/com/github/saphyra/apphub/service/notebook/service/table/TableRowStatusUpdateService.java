package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItem;
import com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableRowStatusUpdateService {
    private final CheckedItemDao checkedItemDao;

    public void setRowStatus(UUID rowId, Boolean status) {
        CheckedItem checkedItem = checkedItemDao.findByIdValidated(rowId);
        checkedItem.setChecked(status);
        checkedItemDao.save(checkedItem);
    }
}
