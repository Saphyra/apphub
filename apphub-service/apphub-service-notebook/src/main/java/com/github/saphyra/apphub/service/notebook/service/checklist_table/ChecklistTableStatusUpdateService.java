package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistTableStatusUpdateService {
    private final ChecklistTableRowDao checklistTableRowDao;

    public void updateStatus(UUID listItemId, Integer rowIndex, Boolean status) {
        ChecklistTableRow row = checklistTableRowDao.findByParentAndRowIndex(listItemId, rowIndex)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.LIST_ITEM_NOT_FOUND.name()), "ChecklistTableRow not found"));
        row.setChecked(status);
        checklistTableRowDao.save(row);
    }
}
