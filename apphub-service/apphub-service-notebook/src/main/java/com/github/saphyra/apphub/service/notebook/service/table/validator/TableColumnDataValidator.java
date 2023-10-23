package com.github.saphyra.apphub.service.notebook.service.table.validator;

import com.github.saphyra.apphub.api.notebook.model.request.FileMetadata;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.ColumnDataServiceFetcher;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import com.github.saphyra.apphub.service.notebook.service.validator.FileMetadataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableColumnDataValidator {
    private final ColumnDataServiceFetcher columnDataServiceFetcher;
    private final ObjectMapperWrapper objectMapperWrapper;
    private final FileMetadataValidator fileRequestValidator;

    public void validate(ColumnType columnType, Object data) {
        columnDataServiceFetcher.findColumnDataService(columnType)
            .validateData(data);

        /*switch (columnType) {
            case NUMBER -> validateNumber(data);
            case TEXT -> ValidationUtil.notNull(data, "textValue");
            case IMAGE, FILE -> validateFile(data);
            case CHECKBOX -> ValidationUtil.parse(data, o -> Boolean.parseBoolean(o.toString()), "checkboxValue");
            case COLOR -> validateColor(data);
            case DATE -> ValidationUtil.parse(data, o -> LocalDate.parse(o.toString()), "dateValue");
            case TIME -> ValidationUtil.parse(data, o -> LocalTime.parse(o.toString()), "timeValue");
            case DATE_TIME -> ValidationUtil.parse(data, o -> LocalDateTime.parse(o.toString()), "dateTimeValue");
            case MONTH -> ValidationUtil.parse(data, o -> LocalDate.parse(o.toString() + "-01"), "monthValue");
            case RANGE -> validateRange(data);
            case LINK -> ValidationUtil.notBlank(data.toString(), "linkValue");
            case EMPTY -> log.debug("No validation required");
            default -> throw ExceptionFactory.notLoggedException(HttpStatus.NOT_IMPLEMENTED, ErrorCode.GENERAL_ERROR, "Unhandled columnType " + columnType);
        }*/
    }

    @ForRemoval("custom-table")
    private void validateRange(Object value) {
        Range range = objectMapperWrapper.convertValue(value, Range.class);

        ValidationUtil.atLeastInclusive(range.getStep(), 0, "rangeStep");
        ValidationUtil.notNull(range.getMin(), "rangeMin");
        ValidationUtil.atLeast(range.getMax(), range.getMin(), "rangeMax");
        ValidationUtil.notNull(range.getValue(), "rangeValue");
    }

    @ForRemoval("custom-table")
    private static void validateColor(Object data) {
        String hexString = data.toString();
        ValidationUtil.length(hexString, 7, "colorValue");
        if (hexString.charAt(0) != '#') {
            throw ExceptionFactory.invalidParam("colorValue", "first character is not #");
        }

        ValidationUtil.parse(hexString, v -> Integer.parseInt(v.toString().substring(1), 16), "colorValue");
    }

    @ForRemoval("custom-table")
    private void validateFile(Object value) {
        if (isNull(value)) {
            return;
        }
        FileMetadata request = objectMapperWrapper.convertValue(value, FileMetadata.class);

        fileRequestValidator.validate(request);
    }

    @ForRemoval("custom-table")
    private void validateNumber(Object value) {
        Number number = objectMapperWrapper.convertValue(value, Number.class);

        ValidationUtil.atLeastInclusive(number.getStep(), 0, "numberStep");
        ValidationUtil.notNull(number.getValue(), "numberValue");
    }
}
