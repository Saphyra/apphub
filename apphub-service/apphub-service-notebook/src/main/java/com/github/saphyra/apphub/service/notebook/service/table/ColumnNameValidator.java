package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class ColumnNameValidator {
    public void validate(String columnName) {
        if (isBlank(columnName)) {
            throw ExceptionFactory.invalidParam("columnName", "must not be null or blank");
        }
    }
}
