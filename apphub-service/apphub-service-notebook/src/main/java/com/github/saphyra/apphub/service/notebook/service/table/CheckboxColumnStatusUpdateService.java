package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckboxColumnStatusUpdateService {
    private final ColumnTypeDao columnTypeDao;
    private final ContentDao contentDao;

    public void updateColumnStatus(UUID columnId, Boolean status) {
        ValidationUtil.notNull(status, "status");

        if (columnTypeDao.findByIdValidated(columnId).getType() != ColumnType.CHECKBOX) {
            throw ExceptionFactory.invalidParam("columnId", "not a " + ColumnType.CHECKBOX);
        }

        Content content = contentDao.findByParentValidated(columnId);
        content.setContent(status.toString());
        contentDao.save(content);
    }
}
