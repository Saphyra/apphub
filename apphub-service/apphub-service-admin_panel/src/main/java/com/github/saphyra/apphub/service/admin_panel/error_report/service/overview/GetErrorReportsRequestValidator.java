package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class GetErrorReportsRequestValidator {
    public static final String DATE_REGEXP = "^(?:[\\+-]?\\d{4}(?!\\d{2}\\b))(?:(-?)(?:(?:0[1-9]|1[0-2])(?:\\1(?:[12]\\d|0[1-9]|3[01]))?|W(?:[0-4]\\d|5[0-2])(?:-?[1-7])?|(?:00[1-9]|0[1-9]\\d|[12]\\d{2}|3(?:[0-5]\\d|6[1-6])))(?:[T\\s](?:(?:(?:[01]\\d|2[0-3])(?:(:?)[0-5]\\d)?|24\\:?00)(?:[\\.,]\\d+(?!:))?)?(?:\\2[0-5]\\d(?:[\\.,]\\d+)?)?(?:[zZ]|(?:[\\+-])(?:[01]\\d|2[0-3]):?(?:[0-5]\\d)?)?)?)?$";
    private static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEXP);

    void validate(GetErrorReportsRequest request) {
        if (!isNull(request.getStatusCode())) {
            tryConvert(() -> HttpStatus.valueOf(request.getStatusCode()), "statusCode", "invalid value");
        }

        if (!isNull(request.getStartTime()) && !DATE_PATTERN.matcher(request.getStartTime()).matches()) {
            throw ExceptionFactory.invalidParam("startTime", "invalid format");
        }

        if (!isNull(request.getEndTime()) && !DATE_PATTERN.matcher(request.getEndTime()).matches()) {
            throw ExceptionFactory.invalidParam("endTime", "invalid format");
        }

        if (!isNull(request.getStatus())) {
            tryConvert(() -> ErrorReportStatus.valueOf(request.getStatus()), "status", "invalid value");
        }

        if (isNull(request.getPageSize())) {
            throw ExceptionFactory.invalidParam("pageSize", "must not be null");
        }

        if (request.getPageSize() < 1) {
            throw ExceptionFactory.invalidParam("pageSize", "too low");
        }

        if (isNull(request.getPage())) {
            throw ExceptionFactory.invalidParam("page", "must not be null");
        }

        if (request.getPage() < 1) {
            throw ExceptionFactory.invalidParam("page", "too low");
        }
    }

    private void tryConvert(Supplier<?> converter, String field, String value) {
        try {
            converter.get();
        } catch (Exception e) {
            throw ExceptionFactory.invalidParam(field, value, e);
        }
    }
}
