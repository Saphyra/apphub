package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistTableStatusUpdateService {
    private final ChecklistTableRowDao checklistTableRowDao;

    public void updateStatus(UUID rowId, Boolean status) {
        ChecklistTableRow row = checklistTableRowDao.findById(rowId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.LIST_ITEM_NOT_FOUND, "ChecklistTableRow not found with rowId " + rowId));
        row.setChecked(status);
        checklistTableRowDao.save(row);
    }
}
