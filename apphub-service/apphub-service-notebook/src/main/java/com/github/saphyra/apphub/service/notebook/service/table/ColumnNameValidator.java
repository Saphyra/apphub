package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.StringUtils.isBlank;


@Component
//TODO unit test
public class ColumnNameValidator {
    public void validate(String columnName) {
        if (isBlank(columnName)) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "columnName", "must not be null or blank"), "ColumnName must not be null or blank");
        }
    }
}
