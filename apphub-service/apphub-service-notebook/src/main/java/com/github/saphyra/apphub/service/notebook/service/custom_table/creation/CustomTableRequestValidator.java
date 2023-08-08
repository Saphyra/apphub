package com.github.saphyra.apphub.service.notebook.service.custom_table.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CustomTableColumnRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRowRequest;
import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.table.join.ColumnType;
import com.github.saphyra.apphub.service.notebook.service.custom_table.data_type.Number;
import com.github.saphyra.apphub.service.notebook.service.custom_table.data_type.Range;
import com.github.saphyra.apphub.service.notebook.service.table.ColumnNameValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.FileMetadataValidator;
import com.github.saphyra.apphub.service.notebook.service.validator.ListItemRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CustomTableRequestValidator {
    private final ColumnNameValidator columnNameValidator;
    private final ListItemRequestValidator listItemRequestValidator;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final FileMetadataValidator fileRequestValidator;

    void validate(CustomTableRequest request) {
        listItemRequestValidator.validate(request.getTitle(), request.getParent());

        request.getColumnNames()
            .forEach(columnNameValidator::validate);

        request.getRows()
            .forEach(row -> validate(row, request.getColumnNames().size()));
    }

    private void validate(CustomTableRowRequest row, int columnAmount) {
        if (row.getColumns().size() != columnAmount) {
            throw ExceptionFactory.invalidParam("columns", "amount different");
        }

        ValidationUtil.notNull(row.getRowIndex(), "rowIndex");

        row.getColumns()
            .forEach(this::validate);
    }

    private void validate(CustomTableColumnRequest column) {
        ValidationUtil.notNull(column.getColumnIndex(), "columnIndex");

        ColumnType columnType = ValidationUtil.convertToEnumChecked(column.getType(), ColumnType::valueOf, "columnType");

        switch (columnType) {
            case NUMBER -> validateNumber(column.getValue());
            case TEXT -> ValidationUtil.notNull(column.getValue(), "textValue");
            case IMAGE, FILE -> validateFile(column.getValue());
            case CHECKBOX -> ValidationUtil.parse(column.getValue(), o -> Boolean.parseBoolean(o.toString()), "checkboxValue");
            case COLOR -> validateColor(column);
            case DATE -> ValidationUtil.parse(column.getValue(), o -> LocalDate.parse(o.toString()), "dateValue");
            case TIME -> ValidationUtil.parse(column.getValue(), o -> LocalTime.parse(o.toString()), "timeValue");
            case DATE_TIME -> ValidationUtil.parse(column.getValue(), o -> LocalDateTime.parse(o.toString()), "dateTimeValue");
            case MONTH -> ValidationUtil.parse(column.getValue(), o -> LocalDate.parse(o.toString() + "-01"), "monthValue");
            case RANGE -> validateRange(column.getValue());
            case LINK -> ValidationUtil.notBlank(column.getValue().toString(), "linkValue");
            case EMPTY -> log.debug("No validation required");
            default -> throw ExceptionFactory.notLoggedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled columnType " + columnType);
        }
    }

    //TODO extract
    private void validateRange(Object value) {
        Range range = objectMapperWrapper.convertValue(value, Range.class);

        ValidationUtil.atLeastInclusive(range.getStep(), 0, "rangeStep");
        ValidationUtil.notNull(range.getMin(), "rangeMin");
        ValidationUtil.atLeast(range.getMax(), range.getMin(), "rangeMax");
        ValidationUtil.notNull(range.getValue(), "rangeValue");
    }

    //TODO extract
    private static void validateColor(CustomTableColumnRequest column) {
        String hexString = column.getValue().toString();
        ValidationUtil.length(hexString, 7, "colorValue");
        if (hexString.charAt(0) != '#') {
            throw ExceptionFactory.invalidParam("colorValue", "first character is not #");
        }

        ValidationUtil.parse(hexString, v -> Integer.parseInt(v.toString().substring(1), 16), "colorValue");
    }

    //TODO extract
    private void validateFile(Object value) {
        if (isNull(value)) {
            return;
        }
        FileMetadata request = objectMapperWrapper.convertValue(value, FileMetadata.class);

        fileRequestValidator.validate(request);
    }

    //TODO extract
    private void validateNumber(Object value) {
        Number number = objectMapperWrapper.convertValue(value, Number.class);

        ValidationUtil.atLeastInclusive(number.getStep(), 0, "numberStep");
        ValidationUtil.notNull(number.getValue(), "numberValue");
    }
}
