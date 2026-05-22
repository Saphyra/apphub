package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRow;
import com.github.saphyra.apphub.service.notebook.dao.list_item.table.row.TableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableRowStatusUpdateService {
    private final TableRowDao tableRowDao;

    public void setRowStatus(UUID userId, UUID listItemId, UUID rowId, Boolean status) {
        ValidationUtil.notNull(status, "status");

        TableRow row = tableRowDao.findByIdValidated(userId, listItemId, rowId);

        row.setChecked(status);

        tableRowDao.save(row);
    }
}
