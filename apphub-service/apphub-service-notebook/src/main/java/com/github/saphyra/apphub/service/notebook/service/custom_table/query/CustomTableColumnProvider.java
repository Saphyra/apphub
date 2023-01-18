package com.github.saphyra.apphub.service.notebook.service.custom_table.query;

import com.github.saphyra.apphub.api.notebook.model.response.TableColumnResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableColumnResponseProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CustomTableColumnProvider implements TableColumnResponseProvider<Object> {
    private final TableJoinDao tableJoinDao;
    private final ContentDao contentDao;
    private final FileDao fileDao;

    @Override
    public List<TableColumnResponse<Object>> fetchTableColumns(UUID listItemId) {
        return tableJoinDao.getByParent(listItemId)
            .stream()
            .map(tableJoin -> TableColumnResponse.<Object>builder()
                .tableJoinId(tableJoin.getTableJoinId())
                .rowIndex(tableJoin.getRowIndex())
                .columnIndex(tableJoin.getColumnIndex())
                .content(getContent(tableJoin))
                .build()
            )
            .collect(Collectors.toList());
    }

    private Object getContent(TableJoin tableJoin) {
        switch (tableJoin.getColumnType()) {
            case NUMBER, TEXT, CHECKBOX, COLOR, DATE, TIME, DATE_TIME, MONTH, RANGE, LINK -> {
                return contentDao.findByParentValidated(tableJoin.getTableJoinId())
                    .getContent();
            }
            case IMAGE, FILE -> {
                return fileDao.findByParentValidated(tableJoin.getTableJoinId())
                    .getStoredFileId();
            }
            case EMPTY -> {
                log.debug("No validation required");
                return null;
            }
            default -> throw ExceptionFactory.notLoggedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled columnType " + tableJoin.getColumnType());
        }
    }
}
